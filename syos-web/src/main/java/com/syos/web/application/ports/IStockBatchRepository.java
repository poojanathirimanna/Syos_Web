package com.syos.web.application.ports;

import com.syos.web.domain.model.StockBatch;

/**
 * Repository interface for Stock Batch operations
 */
public interface IStockBatchRepository {

    /**
     * Save a new stock batch
     * Returns the batch with generated ID
     */
    StockBatch save(StockBatch stockBatch);
}