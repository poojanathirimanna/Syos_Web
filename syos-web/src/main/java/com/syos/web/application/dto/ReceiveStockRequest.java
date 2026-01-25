package com.syos.web.application.dto;

import java.time.LocalDate;

/**
 * DTO for receiving stock from supplier
 * Used in POST /api/admin/inventory/receive
 */
public class ReceiveStockRequest {
    private String productCode;
    private int quantity;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    // Default constructor
    public ReceiveStockRequest() {
    }

    // Constructor
    public ReceiveStockRequest(String productCode, int quantity, LocalDate purchaseDate, LocalDate expiryDate) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
    }

    // Validation
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Purchase date is required");
        }
        if (purchaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Purchase date cannot be in the future");
        }
        if (expiryDate != null && expiryDate.isBefore(purchaseDate)) {
            throw new IllegalArgumentException("Expiry date cannot be before purchase date");
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}