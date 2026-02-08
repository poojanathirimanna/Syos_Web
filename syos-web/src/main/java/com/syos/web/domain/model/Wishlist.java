package com.syos.web.domain.model;

import java.time.LocalDateTime;

/**
 * Wishlist Entity
 * Represents a product in customer's wishlist
 */
public class Wishlist {
    private Integer wishlistId;
    private String userId;
    private String productCode;
    private LocalDateTime addedAt;

    // Default constructor
    public Wishlist() {
    }

    // Constructor without ID
    public Wishlist(String userId, String productCode) {
        this.userId = userId;
        this.productCode = productCode;
    }

    // Full constructor
    public Wishlist(Integer wishlistId, String userId, String productCode, LocalDateTime addedAt) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productCode = productCode;
        this.addedAt = addedAt;
    }

    // Validation
    public void validate() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
    }

    // Getters and Setters
    public Integer getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}