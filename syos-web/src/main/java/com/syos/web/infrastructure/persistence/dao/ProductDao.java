package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.db.Db;
import com.syos.web.domain.enums.ProductStatus;
import com.syos.web.domain.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.syos.web.application.dto.PromotionDTO;

/**
 * Data Access Object for Product
 * Updated to work with inventory_locations table and product_categories
 */
public class ProductDao {

    /**
     * Get all products from database
     */
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT " +
                "    p.product_code, " +
                "    p.name, " +
                "    p.unit_price, " +
                "    p.discount_percentage, " +  // 🆕 NEW
                "    p.discount_start_date, " +   // 🆕 NEW
                "    p.discount_end_date, " +     // 🆕 NEW
                "    p.image_url, " +
                "    p.category_id, " +
                "    pc.category_name, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'SHELF' THEN il.quantity ELSE 0 END), 0) as shelf_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'MAIN' THEN il.quantity ELSE 0 END), 0) as warehouse_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'WEBSITE' THEN il.quantity ELSE 0 END), 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN product_categories pc ON p.category_id = pc.category_id " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code " +
                "WHERE p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name, p.unit_price, p.discount_percentage, p.discount_start_date, p.discount_end_date, p.image_url, p.category_id, pc.category_name " +  // 🆕 UPDATED
                "ORDER BY p.product_code";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                products.add(product);
            }
        }
        return products;
    }

    /**
     * Find product by product code
     */
    public Optional<Product> findByProductCode(String productCode) throws SQLException {
        String sql = "SELECT " +
                "    p.product_code, " +
                "    p.name, " +
                "    p.unit_price, " +
                "    p.discount_percentage, " +  // 🆕 NEW
                "    p.discount_start_date, " +   // 🆕 NEW
                "    p.discount_end_date, " +     // 🆕 NEW
                "    p.image_url, " +
                "    p.category_id, " +
                "    pc.category_name, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'SHELF' THEN il.quantity ELSE 0 END), 0) as shelf_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'MAIN' THEN il.quantity ELSE 0 END), 0) as warehouse_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'WEBSITE' THEN il.quantity ELSE 0 END), 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN product_categories pc ON p.category_id = pc.category_id " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code " +
                "WHERE p.product_code = ? AND p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name, p.unit_price, p.discount_percentage, p.discount_start_date, p.discount_end_date, p.image_url, p.category_id, pc.category_name";  // 🆕 UPDATED

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Check if product exists
     */
    public boolean existsByProductCode(String productCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE product_code = ? AND is_deleted = FALSE";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Insert new product
     */
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO products (product_code, name, unit_price, image_url, category_id, is_deleted) VALUES (?, ?, ?, ?, ?, FALSE)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getUnitPrice());
            stmt.setString(4, product.getImageUrl());

            if (product.getCategoryId() != null) {
                stmt.setInt(5, product.getCategoryId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return product;
            }
            throw new SQLException("Failed to insert product");
        }
    }

    /**
     * Update existing product
     */
    public Product update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, unit_price = ?, image_url = ?, category_id = ? WHERE product_code = ? AND is_deleted = FALSE";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getUnitPrice());
            stmt.setString(3, product.getImageUrl());

            if (product.getCategoryId() != null) {
                stmt.setInt(4, product.getCategoryId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, product.getProductCode());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return product;
            }
            throw new SQLException("Product not found: " + product.getProductCode());
        }
    }

    /**
     * Soft delete product by product code
     */
    public boolean deleteByProductCode(String productCode) throws SQLException {
        String sql = "UPDATE products SET is_deleted = TRUE, deleted_at = NOW() WHERE product_code = ? AND is_deleted = FALSE";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // 🆕 NEW - Set product discount
    public boolean setProductDiscount(String productCode, BigDecimal discountPercentage,
                                      LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "UPDATE products SET discount_percentage = ?, " +
                "discount_start_date = ?, discount_end_date = ?, " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE product_code = ? AND is_deleted = FALSE";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, discountPercentage);
            stmt.setDate(2, startDate != null ? Date.valueOf(startDate) : null);
            stmt.setDate(3, endDate != null ? Date.valueOf(endDate) : null);
            stmt.setString(4, productCode);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // 🆕 NEW - Remove product discount
    public boolean removeProductDiscount(String productCode) throws SQLException {
        return setProductDiscount(productCode, BigDecimal.ZERO, null, null);
    }

    /**
     * Find products with low stock
     */
    public List<Product> findLowStockProducts(int threshold) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT " +
                "    p.product_code, " +
                "    p.name, " +
                "    p.unit_price, " +
                "    p.discount_percentage, " +  // 🆕 NEW
                "    p.discount_start_date, " +   // 🆕 NEW
                "    p.discount_end_date, " +     // 🆕 NEW
                "    p.image_url, " +
                "    p.category_id, " +
                "    pc.category_name, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'SHELF' THEN il.quantity ELSE 0 END), 0) as shelf_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'MAIN' THEN il.quantity ELSE 0 END), 0) as warehouse_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'WEBSITE' THEN il.quantity ELSE 0 END), 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN product_categories pc ON p.category_id = pc.category_id " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code " +
                "WHERE p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name, p.unit_price, p.discount_percentage, p.discount_start_date, p.discount_end_date, p.image_url, p.category_id, pc.category_name " +  // 🆕 UPDATED
                "HAVING warehouse_quantity < ? " +
                "ORDER BY warehouse_quantity ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }

    /**
     * Find out of stock products
     */
    public List<Product> findOutOfStockProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT " +
                "    p.product_code, " +
                "    p.name, " +
                "    p.unit_price, " +
                "    p.discount_percentage, " +  // 🆕 NEW
                "    p.discount_start_date, " +   // 🆕 NEW
                "    p.discount_end_date, " +     // 🆕 NEW
                "    p.image_url, " +
                "    p.category_id, " +
                "    pc.category_name, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'SHELF' THEN il.quantity ELSE 0 END), 0) as shelf_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'MAIN' THEN il.quantity ELSE 0 END), 0) as warehouse_quantity, " +
                "    COALESCE(SUM(CASE WHEN il.location = 'WEBSITE' THEN il.quantity ELSE 0 END), 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN product_categories pc ON p.category_id = pc.category_id " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code " +
                "WHERE p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name, p.unit_price, p.discount_percentage, p.discount_start_date, p.discount_end_date, p.image_url, p.category_id, pc.category_name " +  // 🆕 UPDATED
                "HAVING (shelf_quantity + warehouse_quantity) = 0 " +
                "ORDER BY p.product_code";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    /**
     * Get total count of products
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE is_deleted = FALSE";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Helper method to map ResultSet to Product entity
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        String productCode = rs.getString("product_code");
        String name = rs.getString("name");
        BigDecimal unitPrice = rs.getBigDecimal("unit_price");

        // 🆕 NEW - Map discount fields
        BigDecimal discountPercentage = rs.getBigDecimal("discount_percentage");
        Date startDate = rs.getDate("discount_start_date");
        Date endDate = rs.getDate("discount_end_date");

        String imageUrl = rs.getString("image_url");
        Integer categoryId = (Integer) rs.getObject("category_id");
        int shelfQuantity = rs.getInt("shelf_quantity");
        int warehouseQuantity = rs.getInt("warehouse_quantity");
        int websiteQuantity = rs.getInt("website_quantity");

        Product product = new Product(productCode, name, unitPrice, imageUrl,
                categoryId, shelfQuantity, warehouseQuantity, websiteQuantity);

        // 🆕 NEW - Set discount fields
        product.setDiscountPercentage(discountPercentage != null ? discountPercentage : BigDecimal.ZERO);
        product.setDiscountStartDate(startDate != null ? startDate.toLocalDate() : null);
        product.setDiscountEndDate(endDate != null ? endDate.toLocalDate() : null);

        // Calculate and set status
        product.setStatus(product.calculateStatus());

        return product;
    }

    /**
     * 🆕 NEW - Get all products with active discounts
     */
    public List<PromotionDTO> getAllActiveProductDiscounts() throws SQLException {
        List<PromotionDTO> promotions = new ArrayList<>();
        String sql = "SELECT p.product_code, p.name, p.unit_price, " +
                "p.discount_percentage, p.discount_start_date, p.discount_end_date " +
                "FROM products p " +
                "WHERE p.is_deleted = FALSE " +
                "AND p.discount_percentage > 0 " +
                "AND (p.discount_start_date IS NULL OR p.discount_start_date <= CURDATE()) " +
                "AND (p.discount_end_date IS NULL OR p.discount_end_date >= CURDATE()) " +
                "ORDER BY p.discount_percentage DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String productCode = rs.getString("product_code");
                String productName = rs.getString("name");
                BigDecimal originalPrice = rs.getBigDecimal("unit_price");
                BigDecimal discountPercentage = rs.getBigDecimal("discount_percentage");

                Date startDate = rs.getDate("discount_start_date");
                Date endDate = rs.getDate("discount_end_date");

                // Calculate discounted price
                BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                        discountPercentage.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP)
                );
                BigDecimal discountedPrice = originalPrice.multiply(discountMultiplier)
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                PromotionDTO promotion = new PromotionDTO(
                        productCode,
                        productName,
                        originalPrice,
                        discountedPrice,
                        discountPercentage,
                        startDate != null ? startDate.toLocalDate() : null,
                        endDate != null ? endDate.toLocalDate() : null
                );

                promotions.add(promotion);
            }
        }
        return promotions;
    }
}