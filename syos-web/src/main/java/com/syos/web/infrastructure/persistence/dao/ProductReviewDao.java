package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.ReviewDTO;
import com.syos.web.db.Db;
import com.syos.web.domain.model.ProductReview;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Product Review Data Access Object
 * Handles all database operations for product reviews
 */
public class ProductReviewDao {

    /**
     * Add a new review
     */
    public ProductReview addReview(ProductReview review) throws SQLException {
        String sql = "INSERT INTO product_reviews (product_code, user_id, rating, review_text, is_verified_purchase) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, review.getProductCode());
            stmt.setString(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getReviewText());
            stmt.setBoolean(5, review.isVerifiedPurchase());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        review.setReviewId(rs.getInt(1));
                        System.out.println("✅ Review added: ID=" + review.getReviewId());
                        return review;
                    }
                }
            }

            throw new SQLException("Failed to add review");
        }
    }

    /**
     * Update existing review
     */
    public boolean updateReview(Integer reviewId, String userId, int rating, String reviewText) throws SQLException {
        String sql = "UPDATE product_reviews SET rating = ?, review_text = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE review_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating);
            stmt.setString(2, reviewText);
            stmt.setInt(3, reviewId);
            stmt.setString(4, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Review updated: ID=" + reviewId);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Delete review
     */
    public boolean deleteReview(Integer reviewId, String userId) throws SQLException {
        String sql = "DELETE FROM product_reviews WHERE review_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reviewId);
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ Review deleted: ID=" + reviewId);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Get all reviews for a product
     */
    public List<ReviewDTO> getProductReviews(String productCode) throws SQLException {
        List<ReviewDTO> reviews = new ArrayList<>();
        String sql = "SELECT " +
                "pr.review_id, " +
                "pr.product_code, " +
                "pr.user_id, " +
                "u.username as user_name, " +
                "pr.rating, " +
                "pr.review_text, " +
                "pr.created_at, " +
                "pr.updated_at, " +
                "pr.is_verified_purchase " +
                "FROM product_reviews pr " +
                "JOIN users u ON pr.user_id = u.user_id " +
                "WHERE pr.product_code = ? " +
                "ORDER BY pr.created_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReviewDTO(rs));
                }
            }
        }
        return reviews;
    }

    /**
     * Get all reviews by a user
     */
    public List<ReviewDTO> getUserReviews(String userId) throws SQLException {
        List<ReviewDTO> reviews = new ArrayList<>();
        String sql = "SELECT " +
                "pr.review_id, " +
                "pr.product_code, " +
                "p.name as product_name, " +
                "pr.user_id, " +
                "u.username as user_name, " +
                "pr.rating, " +
                "pr.review_text, " +
                "pr.created_at, " +
                "pr.updated_at, " +
                "pr.is_verified_purchase " +
                "FROM product_reviews pr " +
                "JOIN users u ON pr.user_id = u.user_id " +
                "JOIN products p ON pr.product_code = p.product_code " +
                "WHERE pr.user_id = ? " +
                "ORDER BY pr.created_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReviewDTO dto = mapResultSetToReviewDTO(rs);
                    dto.setProductName(rs.getString("product_name"));
                    reviews.add(dto);
                }
            }
        }
        return reviews;
    }

    /**
     * Check if user has purchased this product (for verified purchase)
     */
    public boolean hasUserPurchasedProduct(String userId, String productCode) throws SQLException {
        String sql = "SELECT COUNT(*) as count " +
                "FROM bills b " +
                "JOIN bill_items bi ON b.bill_number = bi.bill_number " +
                "WHERE b.customer_id = ? AND bi.product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Get average rating for a product
     */
    public double getAverageRating(String productCode) throws SQLException {
        String sql = "SELECT AVG(rating) as avg_rating FROM product_reviews WHERE product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }

    /**
     * Get review count for a product
     */
    public int getReviewCount(String productCode) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM product_reviews WHERE product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * Find review by user and product
     */
    public Optional<ProductReview> findByUserAndProduct(String userId, String productCode) throws SQLException {
        String sql = "SELECT review_id, product_code, user_id, rating, review_text, " +
                "created_at, updated_at, is_verified_purchase " +
                "FROM product_reviews WHERE user_id = ? AND product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReview(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Map ResultSet to ProductReview entity
     */
    private ProductReview mapResultSetToReview(ResultSet rs) throws SQLException {
        Integer reviewId = rs.getInt("review_id");
        String productCode = rs.getString("product_code");
        String userId = rs.getString("user_id");
        int rating = rs.getInt("rating");
        String reviewText = rs.getString("review_text");
        boolean isVerifiedPurchase = rs.getBoolean("is_verified_purchase");

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new ProductReview(
                reviewId,
                productCode,
                userId,
                rating,
                reviewText,
                createdAt != null ? createdAt.toLocalDateTime() : null,
                updatedAt != null ? updatedAt.toLocalDateTime() : null,
                isVerifiedPurchase
        );
    }

    /**
     * Map ResultSet to ReviewDTO
     */
    private ReviewDTO mapResultSetToReviewDTO(ResultSet rs) throws SQLException {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(rs.getInt("review_id"));
        dto.setProductCode(rs.getString("product_code"));
        dto.setUserId(rs.getString("user_id"));
        dto.setUserName(rs.getString("user_name"));
        dto.setRating(rs.getInt("rating"));
        dto.setReviewText(rs.getString("review_text"));
        dto.setVerifiedPurchase(rs.getBoolean("is_verified_purchase"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        dto.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        dto.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

        return dto;
    }
}