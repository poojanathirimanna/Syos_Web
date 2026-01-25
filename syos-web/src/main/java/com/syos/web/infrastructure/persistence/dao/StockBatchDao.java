package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.db.Db;
import com.syos.web.domain.model.StockBatch;

import java.sql.*;

/**
 * Data Access Object for Stock Batches
 */
public class StockBatchDao {

    /**
     * Insert new stock batch
     */
    public StockBatch save(StockBatch stockBatch) throws SQLException {
        String sql = "INSERT INTO stock_batches (product_code, purchase_date, expiry_date, available_quantity, discount_percentage, version) " +
                "VALUES (?, ?, ?, ?, ?, 0)";

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
}