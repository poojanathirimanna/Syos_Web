package com.syos.web.application.dto;

/**
 * DTO for transferring stock between locations
 * Used in POST /api/admin/inventory/transfer
 */
public class TransferStockRequest {
    private String productCode;
    private int quantity;
    private String fromLocation;  // MAIN, SHELF, WEBSITE
    private String toLocation;    // MAIN, SHELF, WEBSITE
    private String userId;        // Who performed the transfer

    // Default constructor
    public TransferStockRequest() {
    }

    // Constructor
    public TransferStockRequest(String productCode, int quantity, String fromLocation, String toLocation, String userId) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.userId = userId;
    }

    // Validation
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("From location is required");
        }
        if (toLocation == null || toLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("To location is required");
        }
        if (!isValidLocation(fromLocation)) {
            throw new IllegalArgumentException("Invalid from location: " + fromLocation + ". Must be MAIN, SHELF, or WEBSITE");
        }
        if (!isValidLocation(toLocation)) {
            throw new IllegalArgumentException("Invalid to location: " + toLocation + ". Must be MAIN, SHELF, or WEBSITE");
        }
        if (fromLocation.equalsIgnoreCase(toLocation)) {
            throw new IllegalArgumentException("From and to locations cannot be the same");
        }
    }

    private boolean isValidLocation(String location) {
        if (location == null) return false;
        String upper = location.toUpperCase();
        return upper.equals("MAIN") || upper.equals("SHELF") || upper.equals("WEBSITE");
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

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}