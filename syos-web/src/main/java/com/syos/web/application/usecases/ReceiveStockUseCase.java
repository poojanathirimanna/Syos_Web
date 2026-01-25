package com.syos.web.application.usecases;

import com.syos.web.application.dto.ReceiveStockRequest;
import com.syos.web.application.dto.StockBatchDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.application.ports.IStockBatchRepository;
import com.syos.web.db.Db;
import com.syos.web.domain.model.StockBatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Use Case for receiving stock from supplier
 * Creates a batch and adds to MAIN warehouse location
 */
public class ReceiveStockUseCase {

    private final IStockBatchRepository stockBatchRepository;
    private final IProductRepository productRepository;

    public ReceiveStockUseCase(IStockBatchRepository stockBatchRepository, IProductRepository productRepository) {
        this.stockBatchRepository = stockBatchRepository;
        this.productRepository = productRepository;
    }

    /**
     * Execute: Receive stock from supplier
     */
    public StockBatchDTO execute(ReceiveStockRequest request) {
        // Step 1: Validate request
        request.validate();

        // Step 2: Check if product exists
        if (!productRepository.existsByProductCode(request.getProductCode())) {
            throw new IllegalArgumentException("Product not found: " + request.getProductCode());
        }

        // Step 3: Create stock batch entity
        StockBatch stockBatch = new StockBatch(
                request.getProductCode(),
                request.getPurchaseDate(),
                request.getExpiryDate(),
                request.getQuantity()
        );

        // Step 4: Validate batch
        stockBatch.validate();

        // Step 5: Save batch to database
        StockBatch savedBatch = stockBatchRepository.save(stockBatch);

        // Step 6: Add to inventory_locations (MAIN warehouse)
        try {
            addToInventoryLocation(savedBatch.getBatchId(), request.getProductCode(), request.getQuantity());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add to inventory location: " + e.getMessage(), e);
        }

        // Step 7: Convert to DTO and return
        return new StockBatchDTO(
                savedBatch.getBatchId(),
                savedBatch.getProductCode(),
                null,  // Product name not needed for this response
                savedBatch.getPurchaseDate(),
                savedBatch.getExpiryDate(),
                savedBatch.getAvailableQuantity(),
                savedBatch.getDiscountPercentage()
        );
    }

    /**
     * Add batch to inventory_locations table (MAIN location)
     */
    private void addToInventoryLocation(Integer batchId, String productCode, int quantity) throws SQLException {
        String sql = "INSERT INTO inventory_locations (product_code, batch_id, location, quantity, version) " +
                "VALUES (?, ?, 'MAIN', ?, 0)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productCode);
            stmt.setInt(2, batchId);
            stmt.setInt(3, quantity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to add to inventory location");
            }
        }
    }
}
