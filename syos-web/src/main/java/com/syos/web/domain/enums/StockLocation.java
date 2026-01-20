package com.syos.web.domain.enums;

/**
 * Enum representing the three-level inventory system
 * MAIN (Warehouse) → SHELF (Store) → WEBSITE (Online)
 */
public enum StockLocation {
    MAIN("Main Warehouse", "main_inventory"),
    SHELF("Shelf Inventory", "shelf_inventory"),
    WEBSITE("Website Inventory", "website_inventory");

    private final String displayName;
    private final String tableName;

    StockLocation(String displayName, String tableName) {
        this.displayName = displayName;
        this.tableName = tableName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Get StockLocation from table name
     */
    public static StockLocation fromTableName(String tableName) {
        for (StockLocation location : StockLocation.values()) {
            if (location.tableName.equalsIgnoreCase(tableName)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
}