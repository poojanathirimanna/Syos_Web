package com.syos.web.application.dto;

import java.math.BigDecimal;

/**
 * DTO for products available for sale (SHELF location)
 * Simplified view for cashiers - no batch details
 */
public class AvailableProductDTO {
    private String productCode;
    private String productName;
    private String categoryName;
    private BigDecimal unitPrice;
    private int availableQuantity;  // Total on SHELF (all batches combined)
    private String imageUrl;

    public AvailableProductDTO() {
    }

    public AvailableProductDTO(String productCode, String productName, String categoryName,
                               BigDecimal unitPrice, int availableQuantity, String imageUrl) {
        this.productCode = productCode;
        this.productName = productName;
        this.categoryName = categoryName;
        this.unitPrice = unitPrice;
        this.availableQuantity = availableQuantity;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}