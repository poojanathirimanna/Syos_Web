package com.syos.web.infrastructure.repositories;

import com.syos.web.application.ports.IStockBatchRepository;
import com.syos.web.domain.model.StockBatch;
import com.syos.web.infrastructure.persistence.dao.StockBatchDao;

import java.sql.SQLException;

/**
 * Implementation of IStockBatchRepository
 */
public class StockBatchRepositoryImpl implements IStockBatchRepository {

    private final StockBatchDao stockBatchDao;

    public StockBatchRepositoryImpl() {
        this.stockBatchDao = new StockBatchDao();
    }

    @Override
    public StockBatch save(StockBatch stockBatch) {
        try {
            return stockBatchDao.save(stockBatch);
        } catch (SQLException e) {
            System.err.println("Error saving stock batch: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save stock batch: " + e.getMessage(), e);
        }
    }
}
