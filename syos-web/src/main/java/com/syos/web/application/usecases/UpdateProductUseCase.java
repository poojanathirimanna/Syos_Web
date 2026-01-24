package com.syos.web.application.usecases;

import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.dto.UpdateProductRequest;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;

import java.util.Optional;

/**
 * Use Case for updating an existing product
 * Handles business logic for product updates
 */
public class UpdateProductUseCase {

    private final IProductRepository productRepository;

    public UpdateProductUseCase(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute the use case: Update an existing product
     * @param request UpdateProductRequest with updated data
     * @return ProductDTO of the updated product
     */
    public ProductDTO execute(UpdateProductRequest request) {
        // Step 1: Validate request
        request.validate();

        // Step 2: Check if product exists
        Optional<Product> existingProductOpt = productRepository.findByProductCode(request.getProductCode());
        if (existingProductOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + request.getProductCode());
        }

        // Step 3: Get existing product and update fields
        Product product = existingProductOpt.get();
        product.setName(request.getName());
        product.setUnitPrice(request.getUnitPrice());

        // Update image URL if provided
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        // Step 4: Validate updated entity
        product.validate();

        // Step 5: Save updated product
        Product updatedProduct = productRepository.update(product);

        // Step 6: Convert to DTO and return
        return new ProductDTO(
                updatedProduct.getProductCode(),
                updatedProduct.getName(),
                updatedProduct.getUnitPrice(),
                updatedProduct.getImageUrl(),
                updatedProduct.getShelfQuantity(),
                updatedProduct.getWarehouseQuantity(),
                updatedProduct.getWebsiteQuantity(),
                updatedProduct.getStatus()
        );
    }
}