package com.syos.web.application.usecases;

import com.syos.web.application.dto.CreateProductRequest;
import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;

/**
 * Use Case for creating a new product
 * Handles business logic for product creation
 */
public class CreateProductUseCase {

    private final IProductRepository productRepository;

    public CreateProductUseCase(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute the use case: Create a new product
     * @param request CreateProductRequest with product data
     * @return ProductDTO of the created product
     */
    public ProductDTO execute(CreateProductRequest request) {
        // Step 1: Validate request
        request.validate();

        // Step 2: Check if product code already exists
        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new IllegalArgumentException("Product with code '" + request.getProductCode() + "' already exists");
        }

        // Step 3: Create domain entity
        Product product = new Product(
                request.getProductCode(),
                request.getName(),
                request.getUnitPrice()
        );

        // Set image URL if provided
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            product.setImageUrl(request.getImageUrl());
        }

        // Step 4: Validate domain entity
        product.validate();

        // Step 5: Save to repository
        Product savedProduct = productRepository.save(product);

        // Step 6: Convert to DTO and return
        return new ProductDTO(
                savedProduct.getProductCode(),
                savedProduct.getName(),
                savedProduct.getUnitPrice(),
                savedProduct.getImageUrl(),
                savedProduct.getShelfQuantity(),
                savedProduct.getWarehouseQuantity(),
                savedProduct.getWebsiteQuantity(),
                savedProduct.getStatus()
        );
    }
}