package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.CartItemDTO;
import com.syos.web.db.Db;
import com.syos.web.domain.model.ShoppingCart;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shopping Cart Data Access Object
 * Handles all database operations for shopping cart
 */
public class ShoppingCartDao {

    /**
     * Get all cart items for a user with product details
     */
    public List<CartItemDTO> getCartItems(String userId) throws SQLException {
        List<CartItemDTO> items = new ArrayList<>();
        String sql = "SELECT " +
                "sc.cart_id, " +
                "sc.product_code, " +
                "p.name as product_name, " +
                "sc.quantity, " +
                "p.unit_price, " +
                "p.discount_percentage, " +
                "p.image_url, " +
                "sc.added_at, " +
                "COALESCE(SUM(il.quantity), 0) as available_quantity " +
                "FROM shopping_cart sc " +
                "JOIN products p ON sc.product_code = p.product_code " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code AND il.location = 'SHELF' " +
                "WHERE sc.user_id = ? " +
                "AND p.is_deleted = FALSE " +
                "GROUP BY sc.cart_id, sc.product_code, p.name, sc.quantity, p.unit_price, p.discount_percentage, p.image_url, sc.added_at " +
                "ORDER BY sc.added_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItemDTO item = new CartItemDTO();
                    item.setCartId(rs.getInt("cart_id"));
                    item.setProductCode(rs.getString("product_code"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));

                    BigDecimal originalPrice = rs.getBigDecimal("unit_price");
                    BigDecimal discountPercentage = rs.getBigDecimal("discount_percentage");

                    // Calculate discounted price
                    BigDecimal unitPrice = originalPrice;
                    if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal discount = discountPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
                        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discount);
                        unitPrice = originalPrice.multiply(discountMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }

                    item.setOriginalPrice(originalPrice);
                    item.setUnitPrice(unitPrice);
                    item.setDiscountPercentage(discountPercentage != null ? discountPercentage : BigDecimal.ZERO);
                    item.setSubtotal(unitPrice.multiply(new BigDecimal(rs.getInt("quantity"))));
                    item.setImageUrl(rs.getString("image_url"));

                    int availableQty = rs.getInt("available_quantity");
                    item.setAvailableQuantity(availableQty);
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
     * Add item to cart or update quantity if already exists
     */
    public ShoppingCart addToCart(String userId, String productCode, int quantity) throws SQLException {
        String sql = "INSERT INTO shopping_cart (user_id, product_code, quantity) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + ?, updated_at = CURRENT_TIMESTAMP";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, userId);
            stmt.setString(2, productCode);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the cart item
                return findByUserAndProduct(userId, productCode).orElse(null);
            }

            throw new SQLException("Failed to add item to cart");
        }
    }

    /**
     * Update cart item quantity
     */
    public boolean updateQuantity(Integer cartId, String userId, int quantity) throws SQLException {
        String sql = "UPDATE shopping_cart SET quantity = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE cart_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, cartId);
            stmt.setString(3, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Remove item from cart
     */
    public boolean removeItem(Integer cartId, String userId) throws SQLException {
        String sql = "DELETE FROM shopping_cart WHERE cart_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Clear entire cart for a user
     */
    public boolean clearCart(String userId) throws SQLException {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            int rowsAffected = stmt.executeUpdate();

            System.out.println("🗑️ Cleared " + rowsAffected + " items from cart for user: " + userId);
            return true;
        }
    }

    /**
     * Find cart item by user and product
     */
    public Optional<ShoppingCart> findByUserAndProduct(String userId, String productCode) throws SQLException {
        String sql = "SELECT cart_id, user_id, product_code, quantity, added_at, updated_at " +
                "FROM shopping_cart WHERE user_id = ? AND product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCart(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get cart item count for a user
     */
    public int getCartItemCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM shopping_cart WHERE user_id = ?";

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

    /**
     * Map ResultSet to ShoppingCart entity
     */
    private ShoppingCart mapResultSetToCart(ResultSet rs) throws SQLException {
        Integer cartId = rs.getInt("cart_id");
        String userId = rs.getString("user_id");
        String productCode = rs.getString("product_code");
        int quantity = rs.getInt("quantity");

        Timestamp addedAt = rs.getTimestamp("added_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new ShoppingCart(
                cartId,
                userId,
                productCode,
                quantity,
                addedAt != null ? addedAt.toLocalDateTime() : null,
                updatedAt != null ? updatedAt.toLocalDateTime() : null
        );
    }
}