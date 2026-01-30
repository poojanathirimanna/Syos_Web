package com.syos.web.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Domain entity representing a Stock Batch
 * Tracks batches with expiry dates for FEFO (First Expire First Out)
 */
public class StockBatch {
    private Integer batchId;
    private String productCode;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private int availableQuantity;

    // 🆕 UPDATED - Discount fields for batch-level discounts
    private BigDecimal discountPercentage;
    private LocalDate discountStartDate;  // 🆕 NEW
    private LocalDate discountEndDate;    // 🆕 NEW

    private int version;  // For optimistic locking

    // Default constructor
    public StockBatch() {
        this.discountPercentage = BigDecimal.ZERO;
        this.discountStartDate = null;  // 🆕 NEW
        this.discountEndDate = null;    // 🆕 NEW
        this.version = 0;
    }

    // Constructor for creating new batch
    public StockBatch(String productCode, LocalDate purchaseDate, LocalDate expiryDate, int availableQuantity) {
        this.productCode = productCode;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.availableQuantity = availableQuantity;
        this.discountPercentage = BigDecimal.ZERO;
        this.discountStartDate = null;  // 🆕 NEW
        this.discountEndDate = null;    // 🆕 NEW
        this.version = 0;
    }

    // Full constructor
    public StockBatch(Integer batchId, String productCode, LocalDate purchaseDate, LocalDate expiryDate,
                      int availableQuantity, BigDecimal discountPercentage, int version) {
        this.batchId = batchId;
        this.productCode = productCode;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.availableQuantity = availableQuantity;
        this.discountPercentage = discountPercentage;
        this.discountStartDate = null;  // 🆕 NEW
        this.discountEndDate = null;    // 🆕 NEW
        this.version = version;
    }

    // 🆕 NEW - Check if batch has active discount
    public boolean hasBatchDiscount() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        LocalDate today = LocalDate.now();

        // No date restrictions - always active
        if (discountStartDate == null && discountEndDate == null) {
            return true;
        }

        // Check if today is within discount period
        boolean afterStart = (discountStartDate == null || !today.isBefore(discountStartDate));
        boolean beforeEnd = (discountEndDate == null || !today.isAfter(discountEndDate));

        return afterStart && beforeEnd;
    }

    // 🆕 NEW - Calculate batch discounted price (requires base price)
    public BigDecimal getBatchDiscountedPrice(BigDecimal basePrice) {
        if (!hasBatchDiscount()) {
            return basePrice;
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );

        return basePrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    // Business logic: Check if batch is expired
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(expiryDate);
    }

    // Business logic: Check if batch is near expiry
    public boolean isNearExpiry(int daysThreshold) {
        if (expiryDate == null) {
            return false;
        }
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return expiryDate.isBefore(thresholdDate) && !isExpired();
    }

    // Business logic: Get days until expiry
    public int getDaysUntilExpiry() {
        if (expiryDate == null) {
            return Integer.MAX_VALUE;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // Business logic: Validate batch data
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Purchase date is required");
        }
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }
        if (expiryDate != null && expiryDate.isBefore(purchaseDate)) {
            throw new IllegalArgumentException("Expiry date cannot be before purchase date");
        }
        if (discountPercentage != null && (discountPercentage.compareTo(BigDecimal.ZERO) < 0 ||
                discountPercentage.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        // 🆕 NEW - Validate discount dates
        if (discountStartDate != null && discountEndDate != null && discountEndDate.isBefore(discountStartDate)) {
            throw new IllegalArgumentException("Discount end date must be after start date");
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

    // 🆕 NEW - Discount date getters and setters
    public LocalDate getDiscountStartDate() {
        return discountStartDate;
    }

    public void setDiscountStartDate(LocalDate discountStartDate) {
        this.discountStartDate = discountStartDate;
    }

    public LocalDate getDiscountEndDate() {
        return discountEndDate;
    }

    public void setDiscountEndDate(LocalDate discountEndDate) {
        this.discountEndDate = discountEndDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "StockBatch{" +
                "batchId=" + batchId +
                ", productCode='" + productCode + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", expiryDate=" + expiryDate +
                ", availableQuantity=" + availableQuantity +
                ", discountPercentage=" + discountPercentage +
                ", version=" + version +
                '}';
    }
}