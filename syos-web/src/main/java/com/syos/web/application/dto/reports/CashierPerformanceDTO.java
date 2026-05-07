package com.syos.web.application.dto.reports;

import java.math.BigDecimal;

/**
 * Cashier Performance Report DTO
 * Used for analyzing individual cashier performance
 */
public class CashierPerformanceDTO {
    private String cashierId;
    private String cashierName;
    private Integer ordersProcessed;
    private BigDecimal totalRevenue;
    private BigDecimal avgOrderValue;
    private String avgProcessingTime; // Format: "MM:SS"
    private Double errorRate;
    private Boolean topPerformer;

    // Constructors
    public CashierPerformanceDTO() {}

    public CashierPerformanceDTO(String cashierId, String cashierName, Integer ordersProcessed,
                                BigDecimal totalRevenue, BigDecimal avgOrderValue, String avgProcessingTime,
                                Double errorRate, Boolean topPerformer) {
        this.cashierId = cashierId;
        this.cashierName = cashierName;
        this.ordersProcessed = ordersProcessed;
        this.totalRevenue = totalRevenue;
        this.avgOrderValue = avgOrderValue;
        this.avgProcessingTime = avgProcessingTime;
        this.errorRate = errorRate;
        this.topPerformer = topPerformer;
    }

    // Getters and Setters
    public String getCashierId() {
        return cashierId;
    }

    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public Integer getOrdersProcessed() {
        return ordersProcessed;
    }

    public void setOrdersProcessed(Integer ordersProcessed) {
        this.ordersProcessed = ordersProcessed;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAvgOrderValue() {
        return avgOrderValue;
    }

    public void setAvgOrderValue(BigDecimal avgOrderValue) {
        this.avgOrderValue = avgOrderValue;
    }

    public String getAvgProcessingTime() {
        return avgProcessingTime;
    }

    public void setAvgProcessingTime(String avgProcessingTime) {
        this.avgProcessingTime = avgProcessingTime;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public Boolean getTopPerformer() {
        return topPerformer;
    }

    public void setTopPerformer(Boolean topPerformer) {
        this.topPerformer = topPerformer;
    }
}
