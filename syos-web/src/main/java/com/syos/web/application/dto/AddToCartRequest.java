package com.syos.web.application.dto;

/**
 * Request DTO for adding items to cart
 */
public class AddToCartRequest {
    private String productCode;
    private int quantity;

    // Default constructor
    public AddToCartRequest() {
    }

    // Full constructor
    public AddToCartRequest(String productCode, int quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    // Validation
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}