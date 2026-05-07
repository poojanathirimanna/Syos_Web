package com.syos.web.application.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Sales Summary Report DTO
 * Used for overall business performance metrics
 */
public class SalesSummaryDTO {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer successfulOrders;
    private Integer cancelledOrders;
    private BigDecimal avgOrderValue;
    private Comparison comparison;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;

    // Inner class for period comparison
    public static class Comparison {
        private BigDecimal previousPeriodRevenue;
        private Double growthPercentage;

        public Comparison() {}

        public Comparison(BigDecimal previousPeriodRevenue, Double growthPercentage) {
            this.previousPeriodRevenue = previousPeriodRevenue;
            this.growthPercentage = growthPercentage;
        }

        // Getters and Setters
        public BigDecimal getPreviousPeriodRevenue() {
            return previousPeriodRevenue;
        }

        public void setPreviousPeriodRevenue(BigDecimal previousPeriodRevenue) {
            this.previousPeriodRevenue = previousPeriodRevenue;
        }

        public Double getGrowthPercentage() {
            return growthPercentage;
        }

        public void setGrowthPercentage(Double growthPercentage) {
            this.growthPercentage = growthPercentage;
        }
    }

    // Constructors
    public SalesSummaryDTO() {}

    // Getters and Setters
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getSuccessfulOrders() {
        return successfulOrders;
    }

    public void setSuccessfulOrders(Integer successfulOrders) {
        this.successfulOrders = successfulOrders;
    }

    public Integer getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Integer cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getAvgOrderValue() {
        return avgOrderValue;
    }

    public void setAvgOrderValue(BigDecimal avgOrderValue) {
        this.avgOrderValue = avgOrderValue;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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
