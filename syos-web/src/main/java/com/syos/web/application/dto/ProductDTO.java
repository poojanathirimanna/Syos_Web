package com.syos.web.application.dto;

import com.syos.web.domain.enums.ProductStatus;

import java.math.BigDecimal;

/**
 * DTO for Product data transfer between layers
 * Used for API responses
 */
public class ProductDTO {
    private String productCode;
    private String name;
    private BigDecimal unitPrice;
    private String imageUrl;  // 🆕 NEW - Product image URL
    private int shelfQuantity;
    private int warehouseQuantity;
    private int websiteQuantity;
    private int totalQuantity;
    private String status;
    private boolean needsReordering;

    // Default constructor
    public ProductDTO() {
    }

    // Constructor with all fields including image
    public ProductDTO(String productCode, String name, BigDecimal unitPrice, String imageUrl,
                      int shelfQuantity, int warehouseQuantity, int websiteQuantity,
                      ProductStatus status) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.websiteQuantity = websiteQuantity;
        this.totalQuantity = shelfQuantity + warehouseQuantity + websiteQuantity;
        this.status = status.getDisplayName();
        this.needsReordering = warehouseQuantity < 50;
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImageUrl() {  // 🆕 NEW
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {  // 🆕 NEW
        this.imageUrl = imageUrl;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(int shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public int getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public void setWarehouseQuantity(int warehouseQuantity) {
        this.warehouseQuantity = warehouseQuantity;
    }

    public int getWebsiteQuantity() {
        return websiteQuantity;
    }

    public void setWebsiteQuantity(int websiteQuantity) {
        this.websiteQuantity = websiteQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNeedsReordering() {
        return needsReordering;
    }

    public void setNeedsReordering(boolean needsReordering) {
        this.needsReordering = needsReordering;
    }
}