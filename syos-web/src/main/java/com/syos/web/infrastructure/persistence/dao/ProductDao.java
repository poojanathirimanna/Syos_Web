package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.db.Db;
import com.syos.web.domain.enums.ProductStatus;
import com.syos.web.domain.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Product
 * Handles raw SQL operations for product table
 */
public class ProductDao {

    /**
     * Get all products from database
     */
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.product_code, p.name, p.unit_price, p.image_url, " +
                "COALESCE(s.shelf_quantity, 0) as shelf_quantity, " +
                "COALESCE(m.warehouse_quantity, 0) as warehouse_quantity, " +
                "COALESCE(w.available_quantity, 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN shelf_inventory s ON p.product_code = s.product_code " +
                "LEFT JOIN main_inventory m ON p.product_code = m.product_code " +
                "LEFT JOIN website_inventory w ON p.product_code = w.product_code " +
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
        String sql = "SELECT p.product_code, p.name, p.unit_price, p.image_url, " +
                "COALESCE(s.shelf_quantity, 0) as shelf_quantity, " +
                "COALESCE(m.warehouse_quantity, 0) as warehouse_quantity, " +
                "COALESCE(w.available_quantity, 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN shelf_inventory s ON p.product_code = s.product_code " +
                "LEFT JOIN main_inventory m ON p.product_code = m.product_code " +
                "LEFT JOIN website_inventory w ON p.product_code = w.product_code " +
                "WHERE p.product_code = ?";

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
        String sql = "SELECT COUNT(*) FROM products WHERE product_code = ?";

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
        String sql = "INSERT INTO products (product_code, name, unit_price, image_url) VALUES (?, ?, ?, ?)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getUnitPrice());
            stmt.setString(4, product.getImageUrl());

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
        String sql = "UPDATE products SET name = ?, unit_price = ?, image_url = ? WHERE product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getUnitPrice());
            stmt.setString(3, product.getImageUrl());
            stmt.setString(4, product.getProductCode());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return product;
            }
            throw new SQLException("Product not found: " + product.getProductCode());
        }
    }

    /**
     * Delete product by product code
     */
    public boolean deleteByProductCode(String productCode) throws SQLException {
        String sql = "DELETE FROM products WHERE product_code = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Find products with low stock
     */
    public List<Product> findLowStockProducts(int threshold) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.product_code, p.name, p.unit_price, p.image_url, " +
                "COALESCE(s.shelf_quantity, 0) as shelf_quantity, " +
                "COALESCE(m.warehouse_quantity, 0) as warehouse_quantity, " +
                "COALESCE(w.available_quantity, 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN shelf_inventory s ON p.product_code = s.product_code " +
                "LEFT JOIN main_inventory m ON p.product_code = m.product_code " +
                "LEFT JOIN website_inventory w ON p.product_code = w.product_code " +
                "WHERE COALESCE(m.warehouse_quantity, 0) < ? " +
                "ORDER BY m.warehouse_quantity ASC";

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
        String sql = "SELECT p.product_code, p.name, p.unit_price, p.image_url, " +
                "COALESCE(s.shelf_quantity, 0) as shelf_quantity, " +
                "COALESCE(m.warehouse_quantity, 0) as warehouse_quantity, " +
                "COALESCE(w.available_quantity, 0) as website_quantity " +
                "FROM products p " +
                "LEFT JOIN shelf_inventory s ON p.product_code = s.product_code " +
                "LEFT JOIN main_inventory m ON p.product_code = m.product_code " +
                "LEFT JOIN website_inventory w ON p.product_code = w.product_code " +
                "WHERE (COALESCE(s.shelf_quantity, 0) + COALESCE(m.warehouse_quantity, 0)) = 0 " +
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
        String sql = "SELECT COUNT(*) FROM products";

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
        String imageUrl = rs.getString("image_url");
        int shelfQuantity = rs.getInt("shelf_quantity");
        int warehouseQuantity = rs.getInt("warehouse_quantity");
        int websiteQuantity = rs.getInt("website_quantity");

        Product product = new Product(productCode, name, unitPrice, imageUrl,
                shelfQuantity, warehouseQuantity, websiteQuantity);

        // Calculate and set status
        product.setStatus(product.calculateStatus());

        return product;
    }
}