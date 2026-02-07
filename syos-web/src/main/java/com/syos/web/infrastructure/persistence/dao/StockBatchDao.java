package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.db.Db;
import com.syos.web.domain.model.StockBatch;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.syos.web.application.dto.PromotionDTO;
import java.time.LocalDate;

/**
 * Data Access Object for Stock Batches
 * 🆕 NOW WITH BATCH DISCOUNT MANAGEMENT!
 */
public class StockBatchDao {

    /**
     * Insert new stock batch
     */
    public StockBatch save(StockBatch stockBatch) throws SQLException {
        String sql = "INSERT INTO stock_batches (product_code, purchase_date, expiry_date, " +
                "available_quantity, discount_percentage, discount_start_date, discount_end_date, version) " +  // 🆕 UPDATED
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";  // 🆕 UPDATED

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, stockBatch.getProductCode());
            stmt.setDate(2, Date.valueOf(stockBatch.getPurchaseDate()));

            if (stockBatch.getExpiryDate() != null) {
                stmt.setDate(3, Date.valueOf(stockBatch.getExpiryDate()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setInt(4, stockBatch.getAvailableQuantity());
            stmt.setBigDecimal(5, stockBatch.getDiscountPercentage());

            // 🆕 NEW - Discount date fields
            if (stockBatch.getDiscountStartDate() != null) {
                stmt.setDate(6, Date.valueOf(stockBatch.getDiscountStartDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            if (stockBatch.getDiscountEndDate() != null) {
                stmt.setDate(7, Date.valueOf(stockBatch.getDiscountEndDate()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get generated batch ID
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        stockBatch.setBatchId(rs.getInt(1));
                        return stockBatch;
                    }
                }
            }

            throw new SQLException("Failed to create stock batch");
        }
    }

    // 🆕 NEW - Find batch by ID
    public Optional<StockBatch> findById(Integer batchId) throws SQLException {
        String sql = "SELECT batch_id, product_code, purchase_date, expiry_date, " +
                "available_quantity, discount_percentage, discount_start_date, " +
                "discount_end_date, version " +
                "FROM stock_batches WHERE batch_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, batchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStockBatch(rs));
                }
            }
        }
        return Optional.empty();
    }

    // 🆕 NEW - Get all batches for a product
    public List<StockBatch> findByProductCode(String productCode) throws SQLException {
        List<StockBatch> batches = new ArrayList<>();
        String sql = "SELECT batch_id, product_code, purchase_date, expiry_date, " +
                "available_quantity, discount_percentage, discount_start_date, " +
                "discount_end_date, version " +
                "FROM stock_batches WHERE product_code = ? " +
                "ORDER BY expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToStockBatch(rs));
                }
            }
        }
        return batches;
    }

    // 🆕 NEW - Set batch discount
    public boolean setBatchDiscount(Integer batchId, BigDecimal discountPercentage,
                                    LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "UPDATE stock_batches SET discount_percentage = ?, " +
                "discount_start_date = ?, discount_end_date = ?, " +
                "last_updated = CURRENT_TIMESTAMP " +
                "WHERE batch_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, discountPercentage);
            stmt.setDate(2, startDate != null ? Date.valueOf(startDate) : null);
            stmt.setDate(3, endDate != null ? Date.valueOf(endDate) : null);
            stmt.setInt(4, batchId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Batch discount set: Batch #" + batchId + " = " + discountPercentage + "%");
            }

            return rowsAffected > 0;
        }
    }

    // 🆕 NEW - Remove batch discount
    public boolean removeBatchDiscount(Integer batchId) throws SQLException {
        System.out.println("🗑️ Removing discount from Batch #" + batchId);
        return setBatchDiscount(batchId, BigDecimal.ZERO, null, null);
    }

    // 🆕 NEW - Get batches near expiry (for automatic discount suggestions)
    public List<StockBatch> findNearExpiryBatches(int daysThreshold) throws SQLException {
        List<StockBatch> batches = new ArrayList<>();
        String sql = "SELECT batch_id, product_code, purchase_date, expiry_date, " +
                "available_quantity, discount_percentage, discount_start_date, " +
                "discount_end_date, version " +
                "FROM stock_batches " +
                "WHERE expiry_date IS NOT NULL " +
                "AND expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "AND available_quantity > 0 " +
                "ORDER BY expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, daysThreshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToStockBatch(rs));
                }
            }
        }
        return batches;
    }

    // 🆕 NEW - Get all batches with active discounts
    public List<StockBatch> findBatchesWithActiveDiscounts() throws SQLException {
        List<StockBatch> batches = new ArrayList<>();
        String sql = "SELECT batch_id, product_code, purchase_date, expiry_date, " +
                "available_quantity, discount_percentage, discount_start_date, " +
                "discount_end_date, version " +
                "FROM stock_batches " +
                "WHERE discount_percentage > 0 " +
                "AND (discount_start_date IS NULL OR discount_start_date <= CURDATE()) " +
                "AND (discount_end_date IS NULL OR discount_end_date >= CURDATE()) " +
                "ORDER BY discount_percentage DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                batches.add(mapResultSetToStockBatch(rs));
            }
        }
        return batches;
    }

    // 🆕 NEW - Map ResultSet to StockBatch
    private StockBatch mapResultSetToStockBatch(ResultSet rs) throws SQLException {
        Integer batchId = rs.getInt("batch_id");
        String productCode = rs.getString("product_code");
        Date purchaseDate = rs.getDate("purchase_date");
        Date expiryDate = rs.getDate("expiry_date");
        int availableQuantity = rs.getInt("available_quantity");
        BigDecimal discountPercentage = rs.getBigDecimal("discount_percentage");
        Date discountStartDate = rs.getDate("discount_start_date");
        Date discountEndDate = rs.getDate("discount_end_date");
        int version = rs.getInt("version");

        StockBatch batch = new StockBatch(
                batchId,
                productCode,
                purchaseDate != null ? purchaseDate.toLocalDate() : null,
                expiryDate != null ? expiryDate.toLocalDate() : null,
                availableQuantity,
                discountPercentage != null ? discountPercentage : BigDecimal.ZERO,
                version
        );

        batch.setDiscountStartDate(discountStartDate != null ? discountStartDate.toLocalDate() : null);
        batch.setDiscountEndDate(discountEndDate != null ? discountEndDate.toLocalDate() : null);

        return batch;
    }
    /**
     * 🆕 NEW - Get all batches with active discounts (with product info)
     */
    /**
     * 🆕 NEW - Get all batches with active discounts (with product info)
     */
    public List<PromotionDTO> getAllActiveBatchDiscounts() throws SQLException {
        List<PromotionDTO> promotions = new ArrayList<>();

        // 🔥 UPDATED SQL - Added sb.available_quantity for debugging
        String sql = "SELECT sb.batch_id, sb.product_code, p.name, p.unit_price, " +
                "sb.expiry_date, sb.available_quantity, sb.discount_percentage, " +  // 🆕 ADDED available_quantity
                "sb.discount_start_date, sb.discount_end_date " +
                "FROM stock_batches sb " +
                "JOIN products p ON sb.product_code = p.product_code " +
                "WHERE sb.discount_percentage > 0 " +
                "AND (sb.discount_start_date IS NULL OR sb.discount_start_date <= CURDATE()) " +
                "AND (sb.discount_end_date IS NULL OR sb.discount_end_date >= CURDATE()) " +
                "AND sb.available_quantity > 0 " +
                "AND p.is_deleted = FALSE " +
                "ORDER BY sb.discount_percentage DESC, sb.expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 🆕 ADD DEBUG LOGGING
            System.out.println("🔍 Executing batch discount query...");
            System.out.println("📅 Current date (CURDATE): " + LocalDate.now());

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;

                while (rs.next()) {
                    count++;
                    Integer batchId = rs.getInt("batch_id");
                    String productCode = rs.getString("product_code");
                    String productName = rs.getString("name");
                    BigDecimal originalPrice = rs.getBigDecimal("unit_price");
                    Date expiryDate = rs.getDate("expiry_date");
                    int availableQty = rs.getInt("available_quantity");  // 🆕 NOW WE CAN READ THIS
                    BigDecimal discountPercentage = rs.getBigDecimal("discount_percentage");
                    Date startDate = rs.getDate("discount_start_date");
                    Date endDate = rs.getDate("discount_end_date");

                    // 🆕 DEBUG LOG
                    System.out.println("  📦 Found Batch #" + batchId + " (" + productCode + "): " +
                            discountPercentage + "% off, Qty: " + availableQty +
                            ", Expires: " + expiryDate);

                    // Calculate discounted price
                    BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                            discountPercentage.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP)
                    );
                    BigDecimal discountedPrice = originalPrice.multiply(discountMultiplier)
                            .setScale(2, java.math.RoundingMode.HALF_UP);

                    // Calculate days until expiry
                    int daysUntilExpiry = Integer.MAX_VALUE;
                    boolean isNearExpiry = false;
                    if (expiryDate != null) {
                        daysUntilExpiry = (int) java.time.temporal.ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                expiryDate.toLocalDate()
                        );
                        isNearExpiry = daysUntilExpiry <= 7; // Near expiry if <= 7 days
                    }

                    PromotionDTO promotion = new PromotionDTO(
                            productCode,
                            productName,
                            batchId,
                            expiryDate != null ? expiryDate.toLocalDate() : null,
                            originalPrice,
                            discountedPrice,
                            discountPercentage,
                            startDate != null ? startDate.toLocalDate() : null,
                            endDate != null ? endDate.toLocalDate() : null,
                            daysUntilExpiry,
                            isNearExpiry
                    );

                    promotions.add(promotion);
                }

                // 🆕 DEBUG LOG
                System.out.println("✅ Total batch discounts found: " + count);
            }
        } catch (SQLException e) {
            // 🆕 ERROR LOGGING
            System.err.println("❌ SQL Error in getAllActiveBatchDiscounts: " + e.getMessage());
            throw e;
        }

        return promotions;
    }
}