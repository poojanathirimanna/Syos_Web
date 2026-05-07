package com.syos.web.application.dto.reports;

import java.math.BigDecimal;

/**
 * Category Performance Report DTO
 * Used for analyzing performance by product category
 */
public class CategoryPerformanceDTO {
    private Integer categoryId;
    private String categoryName;
    private BigDecimal revenue;
    private Integer itemsSold;
    private Integer orderCount;
    private Double percentageOfTotal;

    // Constructors
    public CategoryPerformanceDTO() {}

    public CategoryPerformanceDTO(Integer categoryId, String categoryName, BigDecimal revenue,
                                 Integer itemsSold, Integer orderCount, Double percentageOfTotal) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.revenue = revenue;
        this.itemsSold = itemsSold;
        this.orderCount = orderCount;
        this.percentageOfTotal = percentageOfTotal;
    }

    // Getters and Setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getItemsSold() {
        return itemsSold;
    }

    public void setItemsSold(Integer itemsSold) {
        this.itemsSold = itemsSold;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Double getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(Double percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }
}
