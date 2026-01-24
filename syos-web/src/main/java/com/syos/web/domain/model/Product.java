package com.syos.web.domain.model;

import com.syos.web.domain.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing a Product
 * Core business entity - independent of database/framework
 */
public class Product {
    private String productCode;
    private String name;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Integer categoryId;  // 🆕 NEW - Category association
    private int shelfQuantity;
    private int warehouseQuantity;
    private int websiteQuantity;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public Product(String productCode, String name, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = null;
        this.categoryId = null;  // 🆕 NEW
        this.shelfQuantity = 0;
        this.warehouseQuantity = 0;
        this.websiteQuantity = 0;
        this.status = ProductStatus.OUT_OF_STOCK;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Full constructor with image and category
    public Product(String productCode, String name, BigDecimal unitPrice, String imageUrl,
                   Integer categoryId, int shelfQuantity, int warehouseQuantity, int websiteQuantity) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;  // 🆕 NEW
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.websiteQuantity = websiteQuantity;
        this.status = calculateStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business logic: Calculate product status based on inventory
    public ProductStatus calculateStatus() {
        int totalQuantity = shelfQuantity + warehouseQuantity;
        return ProductStatus.fromQuantity(totalQuantity, 50);
    }

    // Business logic: Check if product needs reordering
    public boolean needsReordering() {
        return warehouseQuantity < 50;
    }

    // Business logic: Get total available quantity
    public int getTotalQuantity() {
        return shelfQuantity + warehouseQuantity + websiteQuantity;
    }

    // Business logic: Validate product data
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        if (shelfQuantity < 0 || warehouseQuantity < 0 || websiteQuantity < 0) {
            throw new IllegalArgumentException("Quantities cannot be negative");
        }
        // categoryId is optional, but if provided, must be positive
        if (categoryId != null && categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
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
        this.updatedAt = LocalDateTime.now();
    }

    // 🆕 NEW - Category getters and setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(int shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
        this.status = calculateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public int getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public void setWarehouseQuantity(int warehouseQuantity) {
        this.warehouseQuantity = warehouseQuantity;
        this.status = calculateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public int getWebsiteQuantity() {
        return websiteQuantity;
    }

    public void setWebsiteQuantity(int websiteQuantity) {
        this.websiteQuantity = websiteQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productCode='" + productCode + '\'' +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", imageUrl='" + imageUrl + '\'' +
                ", categoryId=" + categoryId +
                ", shelfQuantity=" + shelfQuantity +
                ", warehouseQuantity=" + warehouseQuantity +
                ", websiteQuantity=" + websiteQuantity +
                ", status=" + status +
                '}';
    }
}