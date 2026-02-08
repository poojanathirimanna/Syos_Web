package com.syos.web.domain.model;

import com.syos.web.domain.enums.ProductStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private Integer categoryId;
    private BigDecimal discountPercentage;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;
    private int shelfQuantity;
    private int warehouseQuantity;
    private int websiteQuantity;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;  // ✅ Already exists

    // Default constructor
    public Product() {
        this.discountPercentage = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;  // 🆕 ADD THIS
    }

    // Constructor with essential fields
    public Product(String productCode, String name, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = null;
        this.categoryId = null;
        this.discountPercentage = BigDecimal.ZERO;
        this.discountStartDate = null;
        this.discountEndDate = null;
        this.shelfQuantity = 0;
        this.warehouseQuantity = 0;
        this.websiteQuantity = 0;
        this.status = ProductStatus.OUT_OF_STOCK;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;  // 🆕 ADD THIS
    }

    // Full constructor (existing)
    public Product(String productCode, String name, BigDecimal unitPrice, String imageUrl,
                   Integer categoryId, int shelfQuantity, int warehouseQuantity, int websiteQuantity) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.discountPercentage = BigDecimal.ZERO;
        this.discountStartDate = null;
        this.discountEndDate = null;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.websiteQuantity = websiteQuantity;
        this.status = calculateStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;  // 🆕 ADD THIS
    }

    // 🆕 NEW - Full constructor with all fields (for ProductDao)
    public Product(String productCode, String name, BigDecimal unitPrice, String imageUrl,
                   Integer categoryId, int shelfQuantity, int warehouseQuantity, int websiteQuantity,
                   BigDecimal discountPercentage, LocalDate discountStartDate, LocalDate discountEndDate,
                   boolean isDeleted) {
        this.productCode = productCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        this.discountStartDate = discountStartDate;
        this.discountEndDate = discountEndDate;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.websiteQuantity = websiteQuantity;
        this.status = calculateStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = isDeleted;
    }

    // Check if product has active discount
    public boolean hasActiveDiscount() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        LocalDate today = LocalDate.now();

        // No date restrictions - always active
        if (discountStartDate == null && discountEndDate == null) {
            return true;
        }

        // Check if today is within discount period
        boolean afterStart = (discountStartDate == null || !today.isBefore(discountStartDate));
        boolean beforeEnd = (discountEndDate == null || !today.isAfter(discountEndDate));

        return afterStart && beforeEnd;
    }

    // Calculate discounted price
    public BigDecimal getDiscountedPrice() {
        if (!hasActiveDiscount()) {
            return unitPrice;
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );

        return unitPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
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
        if (categoryId != null && categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        if (discountPercentage != null && (discountPercentage.compareTo(BigDecimal.ZERO) < 0 ||
                discountPercentage.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        if (discountStartDate != null && discountEndDate != null && discountEndDate.isBefore(discountStartDate)) {
            throw new IllegalArgumentException("Discount end date must be after start date");
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getDiscountStartDate() {
        return discountStartDate;
    }

    public void setDiscountStartDate(LocalDate discountStartDate) {
        this.discountStartDate = discountStartDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getDiscountEndDate() {
        return discountEndDate;
    }

    public void setDiscountEndDate(LocalDate discountEndDate) {
        this.discountEndDate = discountEndDate;
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

    // 🆕 NEW - isDeleted getter and setter
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Product{" +
                "productCode='" + productCode + '\'' +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", discountPercentage=" + discountPercentage +
                ", imageUrl='" + imageUrl + '\'' +
                ", categoryId=" + categoryId +
                ", shelfQuantity=" + shelfQuantity +
                ", warehouseQuantity=" + warehouseQuantity +
                ", websiteQuantity=" + websiteQuantity +
                ", status=" + status +
                ", isDeleted=" + isDeleted +
                '}';
    }
}