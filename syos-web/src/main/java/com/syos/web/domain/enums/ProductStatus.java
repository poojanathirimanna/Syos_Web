package com.syos.web.domain.enums;

/**
 * Enum representing the status of a product based on inventory levels
 */
public enum ProductStatus {
    IN_STOCK("In Stock"),
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determine product status based on quantity
     * @param quantity Current quantity
     * @param threshold Low stock threshold (e.g., 50)
     * @return ProductStatus
     */
    public static ProductStatus fromQuantity(int quantity, int threshold) {
        if (quantity <= 0) {
            return OUT_OF_STOCK;
        } else if (quantity < threshold) {
            return LOW_STOCK;
        } else {
            return IN_STOCK;
        }
    }
}