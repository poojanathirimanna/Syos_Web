package com.syos.web.application.usecases;

import com.syos.web.application.dto.ProductCatalogDTO;
import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for viewing product catalog
 * Handles business logic for retrieving and displaying all products
 */
public class ProductCatalogUseCase {

    private final IProductRepository productRepository;

    // Constructor injection (Dependency Injection)
    public ProductCatalogUseCase(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute the use case: Get all products and return as catalog DTO
     * @return ProductCatalogDTO containing all products with statistics
     */
    public ProductCatalogDTO execute() {
        // Step 1: Get all products from repository
        List<Product> products = productRepository.findAll();

        // Step 2: Convert domain entities to DTOs
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Step 3: Create and return catalog DTO with statistics
        return new ProductCatalogDTO(productDTOs);
    }

    /**
     * Convert Product entity to ProductDTO
     * @param product Domain entity
     * @return ProductDTO for API response
     */
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getProductCode(),
                product.getName(),
                product.getUnitPrice(),
                product.getImageUrl(),
                product.getCategoryId(),           // 🆕 NEW
                null,                               // 🆕 NEW - categoryName (fetched by DAO)
                product.getShelfQuantity(),
                product.getWarehouseQuantity(),
                product.getWebsiteQuantity(),
                product.getStatus()
        );
    }
}