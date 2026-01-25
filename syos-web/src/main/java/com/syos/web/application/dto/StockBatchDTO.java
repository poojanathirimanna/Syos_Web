package com.syos.web.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Stock Batch response
 */
public class StockBatchDTO {
    private Integer batchId;
    private String productCode;
    private String productName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private int availableQuantity;
    private BigDecimal discountPercentage;
    private int daysUntilExpiry;
    private boolean isExpired;
    private boolean isNearExpiry;

    public StockBatchDTO(Integer batchId, String productCode, String productName, LocalDate purchaseDate,
                         LocalDate expiryDate, int availableQuantity, BigDecimal discountPercentage) {
        this.batchId = batchId;
        this.productCode = productCode;
        this.productName = productName;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.availableQuantity = availableQuantity;
        this.discountPercentage = discountPercentage;

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
    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
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

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
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