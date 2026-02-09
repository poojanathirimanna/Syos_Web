package com.syos.web.application.usecases;

import com.syos.web.application.dto.AddToCartRequest;
import com.syos.web.domain.model.Product;
import com.syos.web.domain.model.ShoppingCart;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Use Case: Add to Cart
 * Adds product to shopping cart or updates quantity
 */
public class AddToCartUseCase {

    private final ShoppingCartDao cartDao;
    private final ProductDao productDao;

    public AddToCartUseCase() {
        this.cartDao = new ShoppingCartDao();
        this.productDao = new ProductDao();
    }

    /**
     * Execute: Add product to cart
     */
    public ShoppingCart execute(String userId, AddToCartRequest request) throws SQLException {
        // Validate request
        request.validate();

        // Check if product exists
        Optional<Product> productOpt = productDao.findByCode(request.getProductCode());
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + request.getProductCode());
        }

        Product product = productOpt.get();

        // Check if product is deleted
        if (product.isDeleted()) {
            throw new IllegalArgumentException("Product is no longer available");
        }

        // Check stock availability - USE WEBSITE QUANTITY FOR CUSTOMERS!
        int availableStock = product.getWebsiteQuantity();
        if (availableStock <= 0) {
            throw new IllegalArgumentException("Product is out of stock");
        }

        // Check if requested quantity is available
        if (request.getQuantity() > availableStock) {
            throw new IllegalArgumentException(
                    "Only " + availableStock + " units available. Requested: " + request.getQuantity()
            );
        }

        // Add to cart (or update if already exists)
        ShoppingCart cart = cartDao.addToCart(userId, request.getProductCode(), request.getQuantity());

        System.out.println("✅ Added to cart: " + request.getProductCode() + " x " + request.getQuantity());

        return cart;
    }
}