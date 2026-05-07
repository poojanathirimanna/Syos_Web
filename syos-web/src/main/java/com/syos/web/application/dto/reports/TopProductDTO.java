package com.syos.web.application.dto.reports;

import java.math.BigDecimal;

/**
 * Top Products Report DTO
 * Used for product performance analysis
 */
public class TopProductDTO {
    private String productCode;
    private String productName;
    private Integer quantitySold;
    private BigDecimal revenue;
    private BigDecimal avgPrice;
    private Integer rank;

    // Constructors
    public TopProductDTO() {}

    public TopProductDTO(String productCode, String productName, Integer quantitySold,
                        BigDecimal revenue, BigDecimal avgPrice, Integer rank) {
        this.productCode = productCode;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.revenue = revenue;
        this.avgPrice = avgPrice;
        this.rank = rank;
    }

    // Getters and Setters
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

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
