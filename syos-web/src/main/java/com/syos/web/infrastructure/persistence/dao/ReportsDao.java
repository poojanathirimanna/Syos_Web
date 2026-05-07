package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.reports.*;
import com.syos.web.db.Db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reports DAO - Handles all reporting queries
 * Implements the backend API specification for SYOS Reporting System
 */
public class ReportsDao {

    /**
     * Get sales summary for a given period
     */
    public SalesSummaryDTO getSalesSummary(LocalDate startDate, LocalDate endDate, String period) throws SQLException {
        SalesSummaryDTO summary = new SalesSummaryDTO();

        // Main query for current period
        String sql = "SELECT " +
                "COUNT(*) as totalOrders, " +
                "SUM(total_amount) as totalRevenue, " +
                "AVG(total_amount) as avgOrderValue, " +
                "SUM(CASE WHEN payment_status = 'PAID' OR payment_status IS NULL THEN 1 ELSE 0 END) as successfulOrders, " +
                "SUM(CASE WHEN order_status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelledOrders " +
                "FROM bills " +
                "WHERE DATE(transaction_date) BETWEEN ? AND ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalOrders(rs.getInt("totalOrders"));
                    summary.setTotalRevenue(rs.getBigDecimal("totalRevenue") != null ?
                        rs.getBigDecimal("totalRevenue") : BigDecimal.ZERO);
                    summary.setAvgOrderValue(rs.getBigDecimal("avgOrderValue") != null ?
                        rs.getBigDecimal("avgOrderValue") : BigDecimal.ZERO);
                    summary.setSuccessfulOrders(rs.getInt("successfulOrders"));
                    summary.setCancelledOrders(rs.getInt("cancelledOrders"));
                }
            }
        }

        // Calculate comparison with previous period
        LocalDate prevStartDate = calculatePreviousPeriodStart(startDate, endDate, period);
        LocalDate prevEndDate = calculatePreviousPeriodEnd(startDate, endDate, period);

        BigDecimal previousRevenue = getPreviousPeriodRevenue(prevStartDate, prevEndDate);
        double growthPercentage = calculateGrowthPercentage(summary.getTotalRevenue(), previousRevenue);

        summary.setComparison(new SalesSummaryDTO.Comparison(previousRevenue, growthPercentage));
        summary.setPeriod(period);
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);

        return summary;
    }

    /**
     * Get top products by quantity or revenue
     */
    public List<TopProductDTO> getTopProducts(LocalDate startDate, LocalDate endDate,
                                            String sortBy, int limit) throws SQLException {
        List<TopProductDTO> topProducts = new ArrayList<>();

        String orderByClause = "quantity".equalsIgnoreCase(sortBy) ?
            "quantitySold DESC" : "revenue DESC";

        String sql = "SELECT " +
                "p.product_code, " +
                "p.name as productName, " +
                "SUM(bi.quantity) as quantitySold, " +
                "SUM(bi.quantity * bi.price_at_sale) as revenue, " +
                "AVG(bi.price_at_sale) as avgPrice " +
                "FROM bill_items bi " +
                "JOIN products p ON bi.product_code = p.product_code " +
                "JOIN bills b ON bi.bill_number = b.bill_number " +
                "WHERE DATE(b.transaction_date) BETWEEN ? AND ? " +
                "GROUP BY p.product_code, p.name " +
                "ORDER BY " + orderByClause + " " +
                "LIMIT ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    TopProductDTO product = new TopProductDTO();
                    product.setProductCode(rs.getString("product_code"));
                    product.setProductName(rs.getString("productName"));
                    product.setQuantitySold(rs.getInt("quantitySold"));
                    product.setRevenue(rs.getBigDecimal("revenue"));
                    product.setAvgPrice(rs.getBigDecimal("avgPrice"));
                    product.setRank(rank++);

                    topProducts.add(product);
                }
            }
        }

        return topProducts;
    }

    /**
     * Get category performance data
     */
    public List<CategoryPerformanceDTO> getCategoryPerformance(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<CategoryPerformanceDTO> categories = new ArrayList<>();

        // First, get total revenue for percentage calculation
        BigDecimal totalRevenue = getTotalRevenue(startDate, endDate);

        String sql = "SELECT " +
                "c.category_id, " +
                "c.category_name, " +
                "SUM(bi.quantity * bi.price_at_sale) as revenue, " +
                "SUM(bi.quantity) as itemsSold, " +
                "COUNT(DISTINCT b.bill_number) as orderCount " +
                "FROM product_categories c " +
                "JOIN products p ON c.category_id = p.category_id " +
                "JOIN bill_items bi ON p.product_code = bi.product_code " +
                "JOIN bills b ON bi.bill_number = b.bill_number " +
                "WHERE DATE(b.transaction_date) BETWEEN ? AND ? " +
                "GROUP BY c.category_id, c.category_name " +
                "ORDER BY revenue DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CategoryPerformanceDTO category = new CategoryPerformanceDTO();
                    category.setCategoryId(rs.getInt("category_id"));
                    category.setCategoryName(rs.getString("category_name"));

                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    category.setRevenue(revenue);
                    category.setItemsSold(rs.getInt("itemsSold"));
                    category.setOrderCount(rs.getInt("orderCount"));

                    // Calculate percentage of total
                    double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                        revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                               .multiply(new BigDecimal("100"))
                               .doubleValue() : 0.0;
                    category.setPercentageOfTotal(percentage);

                    categories.add(category);
                }
            }
        }

        return categories;
    }

    /**
     * Get peak hours analysis
     */
    public List<PeakHoursDTO> getPeakHoursAnalysis(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<PeakHoursDTO> peakHours = new ArrayList<>();

        String sql = "SELECT " +
                "HOUR(transaction_date) as hour, " +
                "COUNT(*) as orderCount, " +
                "SUM(total_amount) as revenue, " +
                "AVG(total_amount) as avgOrderValue " +
                "FROM bills " +
                "WHERE DATE(transaction_date) BETWEEN ? AND ? " +
                "GROUP BY HOUR(transaction_date) " +
                "ORDER BY orderCount DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PeakHoursDTO hour = new PeakHoursDTO();
                    int hourValue = rs.getInt("hour");
                    hour.setHour(hourValue);
                    hour.setHourLabel(formatHourLabel(hourValue));
                    hour.setOrderCount(rs.getInt("orderCount"));
                    hour.setRevenue(rs.getBigDecimal("revenue"));
                    hour.setAvgOrderValue(rs.getBigDecimal("avgOrderValue"));

                    peakHours.add(hour);
                }
            }
        }

        return peakHours;
    }

    /**
     * Get inventory alerts (low stock, expiring soon, out of stock)
     */
    public InventoryAlertsDTO getInventoryAlerts() throws SQLException {
        InventoryAlertsDTO alerts = new InventoryAlertsDTO();

        // Get low stock items
        alerts.setLowStock(getLowStockItems());

        // Get expiring soon items
        alerts.setExpiringSoon(getExpiringSoonItems());

        // Get out of stock items
        alerts.setOutOfStock(getOutOfStockItems());

        // Create summary
        InventoryAlertsDTO.AlertSummary summary = new InventoryAlertsDTO.AlertSummary();
        summary.setLowStockCount(alerts.getLowStock().size());
        summary.setExpiringSoonCount(alerts.getExpiringSoon().size());
        summary.setOutOfStockCount(alerts.getOutOfStock().size());
        alerts.setSummary(summary);

        return alerts;
    }

    /**
     * Get cashier performance data
     */
    public List<CashierPerformanceDTO> getCashierPerformance(LocalDate startDate, LocalDate endDate,
                                                           String cashierId) throws SQLException {
        List<CashierPerformanceDTO> cashiers = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("b.cashier_id, ")
           .append("u.full_name as cashierName, ")
           .append("COUNT(*) as ordersProcessed, ")
           .append("SUM(b.total_amount) as totalRevenue, ")
           .append("AVG(b.total_amount) as avgOrderValue ")
           .append("FROM bills b ")
           .append("JOIN users u ON b.cashier_id = u.user_id ")
           .append("WHERE DATE(b.transaction_date) BETWEEN ? AND ? ")
           .append("AND b.cashier_id IS NOT NULL ");

        if (cashierId != null && !cashierId.trim().isEmpty()) {
            sql.append("AND b.cashier_id = ? ");
        }

        sql.append("GROUP BY b.cashier_id, u.full_name ")
           .append("ORDER BY totalRevenue DESC");

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            if (cashierId != null && !cashierId.trim().isEmpty()) {
                stmt.setString(3, cashierId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                boolean isTopPerformer = true;
                while (rs.next()) {
                    CashierPerformanceDTO cashier = new CashierPerformanceDTO();
                    cashier.setCashierId(rs.getString("cashier_id"));
                    cashier.setCashierName(rs.getString("cashierName"));
                    cashier.setOrdersProcessed(rs.getInt("ordersProcessed"));
                    cashier.setTotalRevenue(rs.getBigDecimal("totalRevenue"));
                    cashier.setAvgOrderValue(rs.getBigDecimal("avgOrderValue"));
                    cashier.setAvgProcessingTime("3:30"); // Placeholder - would need actual timing data
                    cashier.setErrorRate(0.02); // Placeholder - would need error tracking
                    cashier.setTopPerformer(isTopPerformer);

                    cashiers.add(cashier);
                    isTopPerformer = false; // Only the first one is top performer
                }
            }
        }

        return cashiers;
    }

    // Helper methods
    private BigDecimal getPreviousPeriodRevenue(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as revenue FROM bills " +
                    "WHERE DATE(transaction_date) BETWEEN ? AND ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("revenue");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private double calculateGrowthPercentage(BigDecimal currentRevenue, BigDecimal previousRevenue) {
        if (previousRevenue == null || previousRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        return currentRevenue.subtract(previousRevenue)
                           .divide(previousRevenue, 4, RoundingMode.HALF_UP)
                           .multiply(new BigDecimal("100"))
                           .doubleValue();
    }

    private LocalDate calculatePreviousPeriodStart(LocalDate startDate, LocalDate endDate, String period) {
        long daysDifference = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return startDate.minusDays(daysDifference);
    }

    private LocalDate calculatePreviousPeriodEnd(LocalDate startDate, LocalDate endDate, String period) {
        long daysDifference = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return endDate.minusDays(daysDifference);
    }

    private BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as totalRevenue FROM bills " +
                    "WHERE DATE(transaction_date) BETWEEN ? AND ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("totalRevenue");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private String formatHourLabel(int hour) {
        int nextHour = (hour + 1) % 24;
        return String.format("%d:00 %s - %d:00 %s",
                hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour),
                hour < 12 ? "AM" : "PM",
                nextHour == 0 ? 12 : (nextHour > 12 ? nextHour - 12 : nextHour),
                nextHour < 12 ? "AM" : "PM");
    }

    private List<InventoryAlertsDTO.LowStockItem> getLowStockItems() throws SQLException {
        List<InventoryAlertsDTO.LowStockItem> lowStockItems = new ArrayList<>();

        // Note: This assumes you have a min_stock_level field. If not, you can set a default threshold.
        String sql = "SELECT " +
                "p.product_code, " +
                "p.name as productName, " +
                "COALESCE(SUM(il.quantity), 0) as availableQuantity, " +
                "20 as minStockLevel " + // Using 20 as default min stock level
                "FROM products p " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code " +
                "WHERE p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name " +
                "HAVING availableQuantity <= 20 " + // Low stock threshold
                "ORDER BY availableQuantity ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                InventoryAlertsDTO.LowStockItem item = new InventoryAlertsDTO.LowStockItem();
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("productName"));
                item.setAvailableQuantity(rs.getInt("availableQuantity"));
                item.setMinStockLevel(rs.getInt("minStockLevel"));
                item.setDeficit(rs.getInt("minStockLevel") - rs.getInt("availableQuantity"));
                item.setEstimatedOutOfStockDate(LocalDate.now().plusDays(7)); // Estimate

                lowStockItems.add(item);
            }
        }

        return lowStockItems;
    }

    private List<InventoryAlertsDTO.ExpiringSoonItem> getExpiringSoonItems() throws SQLException {
        List<InventoryAlertsDTO.ExpiringSoonItem> expiringSoonItems = new ArrayList<>();

        String sql = "SELECT " +
                "sb.batch_id, " +
                "p.product_code, " +
                "p.name as productName, " +
                "sb.expiry_date, " +
                "DATEDIFF(sb.expiry_date, CURDATE()) as daysUntilExpiry, " +
                "sb.available_quantity as quantity " +
                "FROM stock_batches sb " +
                "JOIN products p ON sb.product_code = p.product_code " +
                "WHERE sb.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) " +
                "AND sb.available_quantity > 0 " +
                "ORDER BY sb.expiry_date ASC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                InventoryAlertsDTO.ExpiringSoonItem item = new InventoryAlertsDTO.ExpiringSoonItem();
                item.setBatchId(rs.getInt("batch_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("productName"));
                item.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                item.setDaysUntilExpiry(rs.getInt("daysUntilExpiry"));
                item.setQuantity(rs.getInt("quantity"));

                expiringSoonItems.add(item);
            }
        }

        return expiringSoonItems;
    }

    private List<InventoryAlertsDTO.OutOfStockItem> getOutOfStockItems() throws SQLException {
        List<InventoryAlertsDTO.OutOfStockItem> outOfStockItems = new ArrayList<>();

        String sql = "SELECT " +
                "p.product_code, " +
                "p.name as productName " +
                "FROM products p " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code " +
                "WHERE p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name " +
                "HAVING COALESCE(SUM(il.quantity), 0) = 0";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                InventoryAlertsDTO.OutOfStockItem item = new InventoryAlertsDTO.OutOfStockItem();
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("productName"));

                outOfStockItems.add(item);
            }
        }

        return outOfStockItems;
    }
}
