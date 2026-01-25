package com.syos.web.application.ports;

import com.syos.web.application.dto.InventoryLocationDTO;

import java.util.List;

/**
 * Repository interface for Inventory Location operations
 * Follows Dependency Inversion Principle
 */
public interface IInventoryLocationRepository {

    /**
     * Get all inventory locations with product and batch details
     */
    List<InventoryLocationDTO> findAll();

    /**
     * Get inventory locations by location type (MAIN, SHELF, WEBSITE)
     */
    List<InventoryLocationDTO> findByLocation(String location);

    /**
     * Get inventory locations for a specific product
     */
    List<InventoryLocationDTO> findByProductCode(String productCode);

    /**
     * Get expired batches
     */
    List<InventoryLocationDTO> findExpiredBatches();

    /**
     * Get batches near expiry (within specified days)
     */
    List<InventoryLocationDTO> findNearExpiryBatches(int daysThreshold);
}