package com.syos.web.application.dto;

import java.math.BigDecimal;

/**
 * DTO for updating an existing product
 * Used in PUT /api/admin/products
 */
public class UpdateProductRequest {
    private String productCode;
    private String name;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Integer categoryId;  // 🆕 NEW - Optional category

    // Default constructor
    public UpdateProductRequest() {
    }

    // Constructor without image and category (for backward compatibility)
    public UpdateProductRequest(String productCode, String name, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = null;
        this.categoryId = null;
    }

    // Constructor with image
    public UpdateProductRequest(String productCode, String name, BigDecimal unitPrice, String imageUrl) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.categoryId = null;
    }

    // 🆕 NEW - Complete constructor with all fields
    public UpdateProductRequest(String productCode, String name, BigDecimal unitPrice, String imageUrl, Integer categoryId) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    // Validation method
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        // imageUrl is optional, so no validation needed
        // categoryId is optional, but if provided, must be positive
        if (categoryId != null && categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 🆕 NEW - Category getters and setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}