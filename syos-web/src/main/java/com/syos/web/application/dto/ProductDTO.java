package com.syos.web.application.dto;

import com.syos.web.domain.enums.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Product data transfer between layers
 * Used for API responses
 */
public class ProductDTO {
    private String productCode;
    private String name;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Integer categoryId;
    private String categoryName;

    // 🆕 NEW - Discount fields
    private BigDecimal discountPercentage;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    private int shelfQuantity;
    private int warehouseQuantity;
    private int websiteQuantity;
    private int totalQuantity;
    private String status;
    private boolean needsReordering;

    // Default constructor
    public ProductDTO() {
    }

    // Constructor with all fields
    public ProductDTO(String productCode, String name, BigDecimal unitPrice, String imageUrl,
                      Integer categoryId, String categoryName, int shelfQuantity, int warehouseQuantity,
                      int websiteQuantity, ProductStatus status) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.discountPercentage = BigDecimal.ZERO;  // 🆕 NEW - Default
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

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

    // 🆕 NEW - Discount getters and setters
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

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