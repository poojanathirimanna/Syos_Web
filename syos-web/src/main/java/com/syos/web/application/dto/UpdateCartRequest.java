package com.syos.web.application.dto;

/**
 * Request DTO for updating cart item quantity
 */
public class UpdateCartRequest {
    private int quantity;

    // Default constructor
    public UpdateCartRequest() {
    }

    // Full constructor
    public UpdateCartRequest(int quantity) {
        this.quantity = quantity;
    }

    // Validation
    public void validate() {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    // Getters and Setters
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}