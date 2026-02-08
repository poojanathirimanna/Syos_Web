package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.WishlistItemDTO;
import com.syos.web.db.Db;
import com.syos.web.domain.model.Wishlist;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Wishlist Data Access Object
 * Handles all database operations for wishlist
 */
public class WishlistDao {

    /**
     * Get all wishlist items for a user with product details
     */
    public List<WishlistItemDTO> getWishlistItems(String userId) throws SQLException {
        List<WishlistItemDTO> items = new ArrayList<>();
        String sql = "SELECT " +
                "w.wishlist_id, " +
                "w.product_code, " +
                "p.name as product_name, " +
                "p.unit_price, " +
                "p.discount_percentage, " +
                "p.image_url, " +
                "w.added_at, " +
                "COALESCE(SUM(il.quantity), 0) as available_quantity " +
                "FROM wishlist w " +
                "JOIN products p ON w.product_code = p.product_code " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code AND il.location = 'SHELF' " +
                "WHERE w.user_id = ? " +
                "AND p.is_deleted = FALSE " +
                "GROUP BY w.wishlist_id, w.product_code, p.name, p.unit_price, p.discount_percentage, p.image_url, w.added_at " +
                "ORDER BY w.added_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WishlistItemDTO item = new WishlistItemDTO();
                    item.setWishlistId(rs.getInt("wishlist_id"));
                    item.setProductCode(rs.getString("product_code"));
                    item.setProductName(rs.getString("product_name"));

                    BigDecimal originalPrice = rs.getBigDecimal("unit_price");
                    BigDecimal discountPercentage = rs.getBigDecimal("discount_percentage");

                    // Calculate discounted price
                    BigDecimal price = originalPrice;
                    if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal discount = discountPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
                        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discount);
                        price = originalPrice.multiply(discountMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }

                    item.setOriginalPrice(originalPrice);
                    item.setPrice(price);
                    item.setDiscountPercentage(discountPercentage != null ? discountPercentage : BigDecimal.ZERO);
                    item.setImageUrl(rs.getString("image_url"));

                    int availableQty = rs.getInt("available_quantity");
                    item.setInStock(availableQty > 0);

                    Timestamp addedAt = rs.getTimestamp("added_at");
                    item.setAddedAt(addedAt != null ? addedAt.toLocalDateTime() : null);

                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Add item to wishlist
     */
    public Wishlist addToWishlist(String userId, String productCode) throws SQLException {
        String sql = "INSERT INTO wishlist (user_id, product_code) VALUES (?, ?)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, userId);
            stmt.setString(2, productCode);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int wishlistId = rs.getInt(1);
                        System.out.println("✅ Added to wishlist: ID=" + wishlistId);
                        return new Wishlist(wishlistId, userId, productCode, LocalDateTime.now());
                    }
                }
            }

            throw new SQLException("Failed to add to wishlist");
        }
    }

    /**
     * Remove item from wishlist by product code
     */
    public boolean removeFromWishlist(String userId, String productCode) throws SQLException {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, productCode);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ Removed from wishlist: " + productCode);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Check if product is in wishlist
     */
    public boolean isInWishlist(String userId, String productCode) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM wishlist WHERE user_id = ? AND product_code = ?";

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
     * Get wishlist item count
     */
    public int getWishlistCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM wishlist WHERE user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
}