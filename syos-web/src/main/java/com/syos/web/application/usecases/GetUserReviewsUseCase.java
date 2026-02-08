package com.syos.web.application.usecases;

import com.syos.web.application.dto.ReviewDTO;
import com.syos.web.infrastructure.persistence.dao.ProductReviewDao;

import java.sql.SQLException;
import java.util.List;

/**
 * Use Case: Get User Reviews
 * Retrieves all reviews by a user
 */
public class GetUserReviewsUseCase {

    private final ProductReviewDao reviewDao;

    public GetUserReviewsUseCase() {
        this.reviewDao = new ProductReviewDao();
    }

    /**
     * Execute: Get user's reviews
     */
    public List<ReviewDTO> execute(String userId) throws SQLException {
        return reviewDao.getUserReviews(userId);
    }
}