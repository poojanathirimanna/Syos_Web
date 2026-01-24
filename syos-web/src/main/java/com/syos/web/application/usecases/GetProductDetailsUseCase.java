package com.syos.web.application.usecases;

import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;

import java.util.Optional;

/**
 * Use Case for getting single product details
 * Handles business logic for retrieving product information
 */
public class GetProductDetailsUseCase {

    private final IProductRepository productRepository;

    public GetProductDetailsUseCase(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute the use case: Get product details by product code
     * @param productCode Product code to retrieve
     * @return ProductDTO with product details
     */
    public ProductDTO execute(String productCode) {
        // Step 1: Validate product code
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }

        // Step 2: Find product in repository
        Optional<Product> productOpt = productRepository.findByProductCode(productCode);

        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + productCode);
        }

        // Step 3: Convert to DTO and return
        Product product = productOpt.get();
        return new ProductDTO(
                product.getProductCode(),
                product.getName(),
                product.getUnitPrice(),
                product.getImageUrl(),
                product.getShelfQuantity(),
                product.getWarehouseQuantity(),
                product.getWebsiteQuantity(),
                product.getStatus()
        );
    }
}