package com.syos.web.application.dto;

/**
 * Request DTO for adding/updating product review
 */
public class AddReviewRequest {
    private String productCode;
    private int rating;
    private String reviewText;

    // Default constructor
    public AddReviewRequest() {
    }

    // Full constructor
    public AddReviewRequest(String productCode, int rating, String reviewText) {
        this.productCode = productCode;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    // Validation
    public void validate() {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
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
}