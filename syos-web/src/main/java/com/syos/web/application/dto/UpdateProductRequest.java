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
    private String imageUrl;  // 🆕 NEW - Optional image URL

    // Default constructor
    public UpdateProductRequest() {
    }

    // Constructor without image (for backward compatibility)
    public UpdateProductRequest(String productCode, String name, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = null;  // Default: keep existing image
    }

    // 🆕 NEW - Constructor with image
    public UpdateProductRequest(String productCode, String name, BigDecimal unitPrice, String imageUrl) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {  // 🆕 NEW
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {  // 🆕 NEW
        this.imageUrl = imageUrl;
    }
}