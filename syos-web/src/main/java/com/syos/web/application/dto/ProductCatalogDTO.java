package com.syos.web.application.dto;

import java.util.List;

/**
 * DTO for Product Catalog response
 * Contains list of products with summary statistics
 */
public class ProductCatalogDTO {
    private List<ProductDTO> products;
    private int totalProducts;
    private int lowStockCount;
    private int outOfStockCount;
    private String message;

    // Default constructor
    public ProductCatalogDTO() {
    }

    // Constructor
    public ProductCatalogDTO(List<ProductDTO> products) {
        this.products = products;
        this.totalProducts = products.size();

        // Calculate statistics
        this.lowStockCount = (int) products.stream()
                .filter(p -> "Low Stock".equals(p.getStatus()))
                .count();

        this.outOfStockCount = (int) products.stream()
                .filter(p -> "Out of Stock".equals(p.getStatus()))
                .count();

        // Generate message
        if (lowStockCount + outOfStockCount > 0) {
            this.message = String.format("Warning: %d products need attention!",
                    lowStockCount + outOfStockCount);
        } else {
            this.message = "All products are in stock";
        }
    }

    // Getters and Setters
    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public int getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(int outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}