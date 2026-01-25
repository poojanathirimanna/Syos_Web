package com.syos.web.application.dto;

import java.util.List;

/**
 * DTO for Inventory Summary
 * Contains list of inventory locations plus summary statistics
 */
public class InventorySummaryDTO {
    private List<InventoryLocationDTO> inventoryLocations;
    private int totalBatches;
    private int totalMainStock;
    private int totalShelfStock;
    private int totalWebsiteStock;
    private int totalStock;
    private int expiredBatchesCount;
    private int nearExpiryBatchesCount;

    public InventorySummaryDTO(List<InventoryLocationDTO> inventoryLocations) {
        this.inventoryLocations = inventoryLocations;
        calculateStatistics();
    }

    private void calculateStatistics() {
        this.totalBatches = inventoryLocations.size();
        this.totalMainStock = 0;
        this.totalShelfStock = 0;
        this.totalWebsiteStock = 0;
        this.expiredBatchesCount = 0;
        this.nearExpiryBatchesCount = 0;

        for (InventoryLocationDTO location : inventoryLocations) {
            switch (location.getLocation()) {
                case "MAIN":
                    this.totalMainStock += location.getQuantity();
                    break;
                case "SHELF":
                    this.totalShelfStock += location.getQuantity();
                    break;
                case "WEBSITE":
                    this.totalWebsiteStock += location.getQuantity();
                    break;
            }

            if (location.isExpired()) {
                this.expiredBatchesCount++;
            } else if (location.isNearExpiry()) {
                this.nearExpiryBatchesCount++;
            }
        }

        this.totalStock = totalMainStock + totalShelfStock + totalWebsiteStock;
    }

    // Getters and Setters
    public List<InventoryLocationDTO> getInventoryLocations() {
        return inventoryLocations;
    }

    public void setInventoryLocations(List<InventoryLocationDTO> inventoryLocations) {
        this.inventoryLocations = inventoryLocations;
    }

    public int getTotalBatches() {
        return totalBatches;
    }

    public void setTotalBatches(int totalBatches) {
        this.totalBatches = totalBatches;
    }

    public int getTotalMainStock() {
        return totalMainStock;
    }

    public void setTotalMainStock(int totalMainStock) {
        this.totalMainStock = totalMainStock;
    }

    public int getTotalShelfStock() {
        return totalShelfStock;
    }

    public void setTotalShelfStock(int totalShelfStock) {
        this.totalShelfStock = totalShelfStock;
    }

    public int getTotalWebsiteStock() {
        return totalWebsiteStock;
    }

    public void setTotalWebsiteStock(int totalWebsiteStock) {
        this.totalWebsiteStock = totalWebsiteStock;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getExpiredBatchesCount() {
        return expiredBatchesCount;
    }

    public void setExpiredBatchesCount(int expiredBatchesCount) {
        this.expiredBatchesCount = expiredBatchesCount;
    }

    public int getNearExpiryBatchesCount() {
        return nearExpiryBatchesCount;
    }

    public void setNearExpiryBatchesCount(int nearExpiryBatchesCount) {
        this.nearExpiryBatchesCount = nearExpiryBatchesCount;
    }
}