package com.syos.web.infrastructure.repositories;

import com.syos.web.application.dto.InventoryLocationDTO;
import com.syos.web.application.ports.IInventoryLocationRepository;
import com.syos.web.infrastructure.persistence.dao.InventoryLocationDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IInventoryLocationRepository
 * Adapts InventoryLocationDao to the repository interface
 */
public class InventoryLocationRepositoryImpl implements IInventoryLocationRepository {

    private final InventoryLocationDao inventoryLocationDao;

    public InventoryLocationRepositoryImpl() {
        this.inventoryLocationDao = new InventoryLocationDao();
    }

    @Override
    public List<InventoryLocationDTO> findAll() {
        try {
            return inventoryLocationDao.findAll();
        } catch (SQLException e) {
            System.err.println("Error finding all inventory locations: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<InventoryLocationDTO> findByLocation(String location) {
        try {
            return inventoryLocationDao.findByLocation(location);
        } catch (SQLException e) {
            System.err.println("Error finding inventory by location: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<InventoryLocationDTO> findByProductCode(String productCode) {
        try {
            return inventoryLocationDao.findByProductCode(productCode);
        } catch (SQLException e) {
            System.err.println("Error finding inventory by product code: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<InventoryLocationDTO> findExpiredBatches() {
        try {
            return inventoryLocationDao.findExpiredBatches();
        } catch (SQLException e) {
            System.err.println("Error finding expired batches: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<InventoryLocationDTO> findNearExpiryBatches(int daysThreshold) {
        try {
            return inventoryLocationDao.findNearExpiryBatches(daysThreshold);
        } catch (SQLException e) {
            System.err.println("Error finding near expiry batches: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}