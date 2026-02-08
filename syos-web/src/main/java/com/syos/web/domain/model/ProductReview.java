package com.syos.web.domain.model;

import java.time.LocalDateTime;

/**
 * Product Review Entity
 * Represents a customer review for a product
 */
public class ProductReview {
    private Integer reviewId;
    private String productCode;
    private String userId;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isVerifiedPurchase;

    // Default constructor
    public ProductReview() {
    }

    // Constructor without ID
    public ProductReview(String productCode, String userId, int rating,
                         String reviewText, boolean isVerifiedPurchase) {
        this.productCode = productCode;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.isVerifiedPurchase = isVerifiedPurchase;
    }

    // Full constructor
    public ProductReview(Integer reviewId, String productCode, String userId,
                         int rating, String reviewText, LocalDateTime createdAt,
                         LocalDateTime updatedAt, boolean isVerifiedPurchase) {
        this.reviewId = reviewId;
        this.productCode = productCode;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isVerifiedPurchase = isVerifiedPurchase;
    }

    // Validation
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
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

    public boolean isVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    public void setVerifiedPurchase(boolean verifiedPurchase) {
        isVerifiedPurchase = verifiedPurchase;
    }
}