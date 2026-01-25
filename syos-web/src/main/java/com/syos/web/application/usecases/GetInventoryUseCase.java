package com.syos.web.application.usecases;

import com.syos.web.application.dto.InventoryLocationDTO;
import com.syos.web.application.dto.InventorySummaryDTO;
import com.syos.web.application.ports.IInventoryLocationRepository;

import java.util.List;

/**
 * Use Case for viewing inventory
 * Handles business logic for retrieving inventory information
 */
public class GetInventoryUseCase {

    private final IInventoryLocationRepository inventoryLocationRepository;

    public GetInventoryUseCase(IInventoryLocationRepository inventoryLocationRepository) {
        this.inventoryLocationRepository = inventoryLocationRepository;
    }

    /**
     * Execute: Get all inventory with summary statistics
     */
    public InventorySummaryDTO execute() {
        List<InventoryLocationDTO> inventoryLocations = inventoryLocationRepository.findAll();
        return new InventorySummaryDTO(inventoryLocations);
    }

    /**
     * Execute: Get inventory by location
     */
    public InventorySummaryDTO executeByLocation(String location) {
        // Validate location
        if (!isValidLocation(location)) {
            throw new IllegalArgumentException("Invalid location: " + location + ". Must be MAIN, SHELF, or WEBSITE");
        }

        List<InventoryLocationDTO> inventoryLocations = inventoryLocationRepository.findByLocation(location);
        return new InventorySummaryDTO(inventoryLocations);
    }

    /**
     * Execute: Get inventory by product code
     */
    public InventorySummaryDTO executeByProduct(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }

        List<InventoryLocationDTO> inventoryLocations = inventoryLocationRepository.findByProductCode(productCode);
        return new InventorySummaryDTO(inventoryLocations);
    }

    /**
     * Execute: Get expired batches
     */
    public InventorySummaryDTO executeExpired() {
        List<InventoryLocationDTO> inventoryLocations = inventoryLocationRepository.findExpiredBatches();
        return new InventorySummaryDTO(inventoryLocations);
    }

    /**
     * Execute: Get near expiry batches
     */
    public InventorySummaryDTO executeNearExpiry(int daysThreshold) {
        if (daysThreshold < 0) {
            throw new IllegalArgumentException("Days threshold must be positive");
        }

        List<InventoryLocationDTO> inventoryLocations = inventoryLocationRepository.findNearExpiryBatches(daysThreshold);
        return new InventorySummaryDTO(inventoryLocations);
    }

    private boolean isValidLocation(String location) {
        return location != null &&
                (location.equals("MAIN") || location.equals("SHELF") || location.equals("WEBSITE"));
    }
}