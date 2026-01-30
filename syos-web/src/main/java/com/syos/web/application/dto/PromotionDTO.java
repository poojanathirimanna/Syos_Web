package com.syos.web.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for displaying promotions/discounts to cashiers
 */
public class PromotionDTO {
    private String type;              // "PRODUCT" or "BATCH"
    private String productCode;
    private String productName;
    private Integer batchId;          // Only for batch discounts
    private LocalDate expiryDate;     // Only for batch discounts
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private int daysUntilExpiry;      // Only for batch discounts
    private boolean isNearExpiry;     // Only for batch discounts

    // Default constructor
    public PromotionDTO() {
    }

    // Constructor for product-level discount
    public PromotionDTO(String productCode, String productName, BigDecimal originalPrice,
                        BigDecimal discountedPrice, BigDecimal discountPercentage,
                        LocalDate startDate, LocalDate endDate) {
        this.type = "PRODUCT";
        this.productCode = productCode;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor for batch-level discount
    public PromotionDTO(String productCode, String productName, Integer batchId,
                        LocalDate expiryDate, BigDecimal originalPrice, BigDecimal discountedPrice,
                        BigDecimal discountPercentage, LocalDate startDate, LocalDate endDate,
                        int daysUntilExpiry, boolean isNearExpiry) {
        this.type = "BATCH";
        this.productCode = productCode;
        this.productName = productName;
        this.batchId = batchId;
        this.expiryDate = expiryDate;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysUntilExpiry = daysUntilExpiry;
        this.isNearExpiry = isNearExpiry;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getDaysUntilExpiry() {
        return daysUntilExpiry;
    }

    public void setDaysUntilExpiry(int daysUntilExpiry) {
        this.daysUntilExpiry = daysUntilExpiry;
    }

    public boolean isNearExpiry() {
        return isNearExpiry;
    }

    public void setNearExpiry(boolean nearExpiry) {
        isNearExpiry = nearExpiry;
    }
}