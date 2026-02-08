package com.syos.web.application.usecases;

import com.syos.web.infrastructure.persistence.dao.ProductReviewDao;

import java.sql.SQLException;

/**
 * Use Case: Delete Product Review
 * Allows customer to delete their review
 */
public class DeleteReviewUseCase {

    private final ProductReviewDao reviewDao;

    public DeleteReviewUseCase() {
        this.reviewDao = new ProductReviewDao();
    }

    /**
     * Execute: Delete review
     */
    public boolean execute(Integer reviewId, String userId) throws SQLException {
        boolean deleted = reviewDao.deleteReview(reviewId, userId);

        if (!deleted) {
            throw new IllegalArgumentException("Review not found or unauthorized");
        }

        return true;
    }
}