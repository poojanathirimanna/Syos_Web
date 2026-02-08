package com.syos.web.application.usecases;

import com.syos.web.application.dto.AddReviewRequest;
import com.syos.web.application.dto.ReviewDTO;
import com.syos.web.domain.model.ProductReview;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.infrastructure.persistence.dao.ProductReviewDao;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Use Case: Add Product Review
 * Allows customer to review a product
 */
public class AddReviewUseCase {

    private final ProductReviewDao reviewDao;
    private final ProductDao productDao;

    public AddReviewUseCase() {
        this.reviewDao = new ProductReviewDao();
        this.productDao = new ProductDao();
    }

    /**
     * Execute: Add review
     */
    public ReviewDTO execute(String userId, AddReviewRequest request) throws SQLException {
        // Validate request
        request.validate();

        // Check if product exists
        if (productDao.findByCode(request.getProductCode()).isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }

        // Check if user already reviewed this product
        Optional<ProductReview> existingReview = reviewDao.findByUserAndProduct(userId, request.getProductCode());
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("You have already reviewed this product. Use update instead.");
        }

        // Check if user purchased this product (for verified purchase badge)
        boolean isVerifiedPurchase = reviewDao.hasUserPurchasedProduct(userId, request.getProductCode());

        // Create review
        ProductReview review = new ProductReview(
                request.getProductCode(),
                userId,
                request.getRating(),
                request.getReviewText(),
                isVerifiedPurchase
        );

        review.validate();

        // Save review
        ProductReview savedReview = reviewDao.addReview(review);

        // Convert to DTO
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(savedReview.getReviewId());
        dto.setProductCode(savedReview.getProductCode());
        dto.setUserId(savedReview.getUserId());
        dto.setRating(savedReview.getRating());
        dto.setReviewText(savedReview.getReviewText());
        dto.setVerifiedPurchase(savedReview.isVerifiedPurchase());
        dto.setCreatedAt(savedReview.getCreatedAt());

        return dto;
    }
}