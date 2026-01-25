package com.syos.web.application.usecases;

import com.syos.web.application.dto.TransferStockRequest;
import com.syos.web.application.dto.TransferStockResponse;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.db.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case for transferring stock between locations using FEFO
 * Calls the stored procedure: transfer_stock_fefo
 */
public class TransferStockUseCase {

    private final IProductRepository productRepository;

    public TransferStockUseCase(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute: Transfer stock using FEFO stored procedure
     */
    public TransferStockResponse execute(TransferStockRequest request) {
        // Step 1: Validate request
        request.validate();

        // Step 2: Check if product exists
        if (!productRepository.existsByProductCode(request.getProductCode())) {
            throw new IllegalArgumentException("Product not found: " + request.getProductCode());
        }

        // Step 3: Call stored procedure to transfer stock using FEFO
        try {
            return callTransferStockProcedure(request);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to transfer stock: " + e.getMessage(), e);
        }
    }

    /**
     * Call the transfer_stock_fefo stored procedure
     */
    private TransferStockResponse callTransferStockProcedure(TransferStockRequest request) throws SQLException {
        String sql = "{CALL transfer_stock_fefo(?, ?, ?, ?, ?)}";

        try (Connection conn = Db.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set input parameters
            stmt.setString(1, request.getProductCode());
            stmt.setString(2, request.getFromLocation().toUpperCase());
            stmt.setString(3, request.getToLocation().toUpperCase());
            stmt.setInt(4, request.getQuantity());
            stmt.setString(5, request.getUserId() != null ? request.getUserId() : "system");

            // Execute stored procedure
            boolean hasResults = stmt.execute();

            // Get batches used (if procedure returns result set)
            List<TransferStockResponse.BatchTransfer> batchesUsed = new ArrayList<>();

            if (hasResults) {
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        int batchId = rs.getInt("batch_id");
                        int qtyTransferred = rs.getInt("quantity_transferred");
                        Date expiryDate = rs.getDate("expiry_date");

                        batchesUsed.add(new TransferStockResponse.BatchTransfer(
                                batchId,
                                qtyTransferred,
                                expiryDate != null ? expiryDate.toString() : null
                        ));
                    }
                }
            }

            // Build response
            String message = batchesUsed.isEmpty()
                    ? "Stock transferred successfully using FEFO"
                    : "Stock transferred from " + batchesUsed.size() + " batch(es) using FEFO";

            return new TransferStockResponse(
                    request.getProductCode(),
                    request.getQuantity(),
                    request.getFromLocation().toUpperCase(),
                    request.getToLocation().toUpperCase(),
                    batchesUsed,
                    message
            );

        } catch (SQLException e) {
            // Handle specific SQL errors
            if (e.getMessage().contains("Insufficient stock")) {
                throw new IllegalArgumentException("Insufficient stock in " + request.getFromLocation() +
                        " location for product " + request.getProductCode());
            }
            throw e;
        }
    }
}