package com.syos.web.application.dto.reports;

import java.math.BigDecimal;

/**
 * Peak Hours Analysis DTO
 * Used for analyzing sales patterns by hour
 */
public class PeakHoursDTO {
    private Integer hour;
    private String hourLabel;
    private Integer orderCount;
    private BigDecimal revenue;
    private BigDecimal avgOrderValue;

    // Constructors
    public PeakHoursDTO() {}

    public PeakHoursDTO(Integer hour, String hourLabel, Integer orderCount,
                       BigDecimal revenue, BigDecimal avgOrderValue) {
        this.hour = hour;
        this.hourLabel = hourLabel;
        this.orderCount = orderCount;
        this.revenue = revenue;
        this.avgOrderValue = avgOrderValue;
    }

    // Getters and Setters
    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public String getHourLabel() {
        return hourLabel;
    }

    public void setHourLabel(String hourLabel) {
        this.hourLabel = hourLabel;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getAvgOrderValue() {
        return avgOrderValue;
    }

    public void setAvgOrderValue(BigDecimal avgOrderValue) {
        this.avgOrderValue = avgOrderValue;
    }
}
