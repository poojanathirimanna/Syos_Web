package com.syos.web.application.usecases;

import com.syos.web.application.ports.IProductRepository;

/**
 * Use Case for deleting a product
 * Handles business logic for product deletion (soft delete)
 */
public class DeleteProductUseCase {

    private final IProductRepository productRepository;

    public DeleteProductUseCase(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute the use case: Delete a product (soft delete)
     * @param productCode Product code to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean execute(String productCode) {
        // Step 1: Validate product code
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }

        // Step 2: Check if product exists
        if (!productRepository.existsByProductCode(productCode)) {
            throw new IllegalArgumentException("Product not found: " + productCode);
        }

        // Step 3: Perform soft delete
        return productRepository.deleteByProductCode(productCode);
    }
}