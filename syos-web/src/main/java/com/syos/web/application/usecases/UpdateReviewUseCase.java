package com.syos.web.application.usecases;

import com.syos.web.application.dto.AddReviewRequest;
import com.syos.web.infrastructure.persistence.dao.ProductReviewDao;

import java.sql.SQLException;

/**
 * Use Case: Update Product Review
 * Allows customer to update their review
 */
public class UpdateReviewUseCase {

    private final ProductReviewDao reviewDao;

    public UpdateReviewUseCase() {
        this.reviewDao = new ProductReviewDao();
    }

    /**
     * Execute: Update review
     */
    public boolean execute(Integer reviewId, String userId, AddReviewRequest request) throws SQLException {
        // Validate request
        request.validate();

        // Update review
        boolean updated = reviewDao.updateReview(reviewId, userId, request.getRating(), request.getReviewText());

        if (!updated) {
            throw new IllegalArgumentException("Review not found or unauthorized");
        }

        return true;
    }
}