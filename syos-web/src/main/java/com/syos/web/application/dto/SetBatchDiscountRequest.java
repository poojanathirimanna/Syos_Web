package com.syos.web.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for setting batch discount
 */
public class SetBatchDiscountRequest {
    private Integer batchId;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;

    public void validate() {
        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID is required");
        }

        if (discountPercentage == null) {
            throw new IllegalArgumentException("Discount percentage is required");
        }

        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 ||
                discountPercentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    // Getters and setters
    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
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
}