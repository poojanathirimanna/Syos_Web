package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.InventoryLocationDTO;
import com.syos.web.db.Db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Inventory Locations
 * Handles database operations for inventory_locations table
 */
public class InventoryLocationDao {

    /**
     * Get all inventory locations with product and batch info
     */
    public List<InventoryLocationDTO> findAll() throws SQLException {
        List<InventoryLocationDTO> inventoryLocations = new ArrayList<>();

        String sql = "SELECT " +
                "    il.id, " +
                "    il.product_code, " +
                "    p.name as product_name, " +
                "    il.batch_id, " +
                "    il.location, " +
                "    il.quantity, " +
                "    sb.purchase_date, " +
                "    sb.expiry_date " +
                "FROM inventory_locations il " +
                "JOIN products p ON il.product_code = p.product_code " +
                "JOIN stock_batches sb ON il.batch_id = sb.batch_id " +
                "WHERE il.quantity > 0 AND p.is_deleted = FALSE " +
                "ORDER BY sb.expiry_date ASC, il.location, p.name";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inventoryLocations.add(mapResultSetToDTO(rs));
            }
        }

        return inventoryLocations;
    }

    /**
     * Get inventory locations by location type
     */
    public List<InventoryLocationDTO> findByLocation(String location) throws SQLException {
        List<InventoryLocationDTO> inventoryLocations = new ArrayList<>();

        String sql = "SELECT " +
                "    il.id, " +
                "    il.product_code, " +
                "    p.name as product_name, " +
                "    il.batch_id, " +
                "    il.location, " +
                "    il.quantity, " +
                "    sb.purchase_date, " +
                "    sb.expiry_date " +
                "FROM inventory_locations il " +
                "JOIN products p ON il.product_code = p.product_code " +
                "JOIN stock_batches sb ON il.batch_id = sb.batch_id " +
                "WHERE il.location = ? AND il.quantity > 0 AND p.is_deleted = FALSE " +
                "ORDER BY sb.expiry_date ASC, p.name";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventoryLocations.add(mapResultSetToDTO(rs));
                }
            }
        }

        return inventoryLocations;
    }

    /**
     * Get inventory locations for a specific product
     */
    public List<InventoryLocationDTO> findByProductCode(String productCode) throws SQLException {
        List<InventoryLocationDTO> inventoryLocations = new ArrayList<>();

        String sql = "SELECT " +
                "    il.id, " +
                "    il.product_code, " +
                "    p.name as product_name, " +
                "    il.batch_id, " +
                "    il.location, " +
                "    il.quantity, " +
                "    sb.purchase_date, " +
                "    sb.expiry_date " +
                "FROM inventory_locations il " +
                "JOIN products p ON il.product_code = p.product_code " +
                "JOIN stock_batches sb ON il.batch_id = sb.batch_id " +
                "WHERE il.product_code = ? AND il.quantity > 0 AND p.is_deleted = FALSE " +
                "ORDER BY il.location, sb.expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventoryLocations.add(mapResultSetToDTO(rs));
                }
            }
        }

        return inventoryLocations;
    }

    /**
     * Get expired batches
     */
    public List<InventoryLocationDTO> findExpiredBatches() throws SQLException {
        List<InventoryLocationDTO> inventoryLocations = new ArrayList<>();

        String sql = "SELECT " +
                "    il.id, " +
                "    il.product_code, " +
                "    p.name as product_name, " +
                "    il.batch_id, " +
                "    il.location, " +
                "    il.quantity, " +
                "    sb.purchase_date, " +
                "    sb.expiry_date " +
                "FROM inventory_locations il " +
                "JOIN products p ON il.product_code = p.product_code " +
                "JOIN stock_batches sb ON il.batch_id = sb.batch_id " +
                "WHERE sb.expiry_date < CURDATE() AND il.quantity > 0 AND p.is_deleted = FALSE " +
                "ORDER BY sb.expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inventoryLocations.add(mapResultSetToDTO(rs));
            }
        }

        return inventoryLocations;
    }

    /**
     * Get batches near expiry
     */
    public List<InventoryLocationDTO> findNearExpiryBatches(int daysThreshold) throws SQLException {
        List<InventoryLocationDTO> inventoryLocations = new ArrayList<>();

        String sql = "SELECT " +
                "    il.id, " +
                "    il.product_code, " +
                "    p.name as product_name, " +
                "    il.batch_id, " +
                "    il.location, " +
                "    il.quantity, " +
                "    sb.purchase_date, " +
                "    sb.expiry_date " +
                "FROM inventory_locations il " +
                "JOIN products p ON il.product_code = p.product_code " +
                "JOIN stock_batches sb ON il.batch_id = sb.batch_id " +
                "WHERE sb.expiry_date >= CURDATE() " +
                "  AND sb.expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "  AND il.quantity > 0 AND p.is_deleted = FALSE " +
                "ORDER BY sb.expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, daysThreshold);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventoryLocations.add(mapResultSetToDTO(rs));
                }
            }
        }

        return inventoryLocations;
    }

    /**
     * Map ResultSet to InventoryLocationDTO
     */
    private InventoryLocationDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String productCode = rs.getString("product_code");
        String productName = rs.getString("product_name");
        Integer batchId = rs.getInt("batch_id");
        String location = rs.getString("location");
        int quantity = rs.getInt("quantity");

        Date purchaseDateSql = rs.getDate("purchase_date");
        LocalDate purchaseDate = purchaseDateSql != null ? purchaseDateSql.toLocalDate() : null;

        Date expiryDateSql = rs.getDate("expiry_date");
        LocalDate expiryDate = expiryDateSql != null ? expiryDateSql.toLocalDate() : null;

        return new InventoryLocationDTO(id, productCode, productName, batchId,
                location, quantity, purchaseDate, expiryDate);
    }
}