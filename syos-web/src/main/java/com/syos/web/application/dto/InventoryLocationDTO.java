package com.syos.web.application.dto;

import java.time.LocalDate;

/**
 * DTO for Inventory Location data
 * Displays stock information by location and batch
 */
public class InventoryLocationDTO {
    private Long id;
    private String productCode;
    private String productName;
    private Integer batchId;
    private String location;  // MAIN, SHELF, WEBSITE
    private int quantity;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private int daysUntilExpiry;
    private boolean isExpired;
    private boolean isNearExpiry;  // Within 30 days

    // Default constructor
    public InventoryLocationDTO() {
    }

    // Full constructor
    public InventoryLocationDTO(Long id, String productCode, String productName, Integer batchId,
                                String location, int quantity, LocalDate purchaseDate, LocalDate expiryDate) {
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.batchId = batchId;
        this.location = location;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;

        // Calculate expiry info
        if (expiryDate != null) {
            LocalDate today = LocalDate.now();
            this.daysUntilExpiry = (int) java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
            this.isExpired = daysUntilExpiry < 0;
            this.isNearExpiry = daysUntilExpiry >= 0 && daysUntilExpiry <= 30;
        } else {
            this.daysUntilExpiry = Integer.MAX_VALUE;
            this.isExpired = false;
            this.isNearExpiry = false;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public int getDaysUntilExpiry() {
        return daysUntilExpiry;
    }

    public void setDaysUntilExpiry(int daysUntilExpiry) {
        this.daysUntilExpiry = daysUntilExpiry;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isNearExpiry() {
        return isNearExpiry;
    }

    public void setNearExpiry(boolean nearExpiry) {
        isNearExpiry = nearExpiry;
    }
}