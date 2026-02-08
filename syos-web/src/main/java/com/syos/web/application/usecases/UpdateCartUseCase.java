package com.syos.web.application.usecases;

import com.syos.web.application.dto.UpdateCartRequest;
import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;

import java.sql.SQLException;

/**
 * Use Case: Update Cart Item
 * Updates quantity of item in cart
 */
public class UpdateCartUseCase {

    private final ShoppingCartDao cartDao;

    public UpdateCartUseCase() {
        this.cartDao = new ShoppingCartDao();
    }

    /**
     * Execute: Update cart item quantity
     */
    public boolean execute(Integer cartId, String userId, UpdateCartRequest request) throws SQLException {
        // Validate request
        request.validate();

        // Update quantity
        boolean updated = cartDao.updateQuantity(cartId, userId, request.getQuantity());

        if (!updated) {
            throw new IllegalArgumentException("Cart item not found or unauthorized");
        }

        System.out.println("✅ Cart updated: ID=" + cartId + ", New quantity=" + request.getQuantity());

        return true;
    }
}