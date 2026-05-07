package com.syos.web.application.dto.reports;

import java.time.LocalDate;
import java.util.List;

/**
 * Inventory Alerts DTO
 * Used for monitoring inventory status and alerts
 */
public class InventoryAlertsDTO {
    private List<LowStockItem> lowStock;
    private List<ExpiringSoonItem> expiringSoon;
    private List<OutOfStockItem> outOfStock;
    private AlertSummary summary;

    // Inner classes for different alert types
    public static class LowStockItem {
        private String productCode;
        private String productName;
        private Integer availableQuantity;
        private Integer minStockLevel;
        private Integer deficit;
        private LocalDate estimatedOutOfStockDate;

        public LowStockItem() {}

        // Getters and Setters
        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

        public Integer getMinStockLevel() { return minStockLevel; }
        public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

        public Integer getDeficit() { return deficit; }
        public void setDeficit(Integer deficit) { this.deficit = deficit; }

        public LocalDate getEstimatedOutOfStockDate() { return estimatedOutOfStockDate; }
        public void setEstimatedOutOfStockDate(LocalDate estimatedOutOfStockDate) { this.estimatedOutOfStockDate = estimatedOutOfStockDate; }
    }

    public static class ExpiringSoonItem {
        private Integer batchId;
        private String productCode;
        private String productName;
        private LocalDate expiryDate;
        private Integer daysUntilExpiry;
        private Integer quantity;

        public ExpiringSoonItem() {}

        // Getters and Setters
        public Integer getBatchId() { return batchId; }
        public void setBatchId(Integer batchId) { this.batchId = batchId; }

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

        public Integer getDaysUntilExpiry() { return daysUntilExpiry; }
        public void setDaysUntilExpiry(Integer daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class OutOfStockItem {
        private String productCode;
        private String productName;

        public OutOfStockItem() {}

        // Getters and Setters
        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
    }

    public static class AlertSummary {
        private Integer lowStockCount;
        private Integer expiringSoonCount;
        private Integer outOfStockCount;

        public AlertSummary() {}

        public AlertSummary(Integer lowStockCount, Integer expiringSoonCount, Integer outOfStockCount) {
            this.lowStockCount = lowStockCount;
            this.expiringSoonCount = expiringSoonCount;
            this.outOfStockCount = outOfStockCount;
        }

        // Getters and Setters
        public Integer getLowStockCount() { return lowStockCount; }
        public void setLowStockCount(Integer lowStockCount) { this.lowStockCount = lowStockCount; }

        public Integer getExpiringSoonCount() { return expiringSoonCount; }
        public void setExpiringSoonCount(Integer expiringSoonCount) { this.expiringSoonCount = expiringSoonCount; }

        public Integer getOutOfStockCount() { return outOfStockCount; }
        public void setOutOfStockCount(Integer outOfStockCount) { this.outOfStockCount = outOfStockCount; }
    }

    // Constructors
    public InventoryAlertsDTO() {}

    // Getters and Setters
    public List<LowStockItem> getLowStock() {
        return lowStock;
    }

    public void setLowStock(List<LowStockItem> lowStock) {
        this.lowStock = lowStock;
    }

    public List<ExpiringSoonItem> getExpiringSoon() {
        return expiringSoon;
    }

    public void setExpiringSoon(List<ExpiringSoonItem> expiringSoon) {
        this.expiringSoon = expiringSoon;
    }

    public List<OutOfStockItem> getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(List<OutOfStockItem> outOfStock) {
        this.outOfStock = outOfStock;
    }

    public AlertSummary getSummary() {
        return summary;
    }

    public void setSummary(AlertSummary summary) {
        this.summary = summary;
    }
}
