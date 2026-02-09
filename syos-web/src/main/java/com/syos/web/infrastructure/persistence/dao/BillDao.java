package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.BillItemDTO;
import com.syos.web.db.Db;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Bills
 * 🆕 NOW SUPPORTS BOTH CASHIER BILLS AND CUSTOMER ORDERS
 */
public class BillDao {

    /**
     * Generate unique bill number (format: BILL-YYYYMMDD-XXXXXX)
     */
    private String generateBillNumber() throws SQLException {
        String prefix = "BILL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";

        String sql = "SELECT COUNT(*) as count FROM bills WHERE bill_number LIKE ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, prefix + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count") + 1;
                    return prefix + String.format("%06d", count);
                }
            }
        }

        return prefix + "000001";
    }

    /**
     * 🆕 UPDATED - Create a new bill (supports both cashier and customer orders)
     * Returns the bill number
     */
    public String createBill(
            String userId,
            String userType,
            String channel,
            String paymentMethod,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            BigDecimal amountPaid,
            BigDecimal changeAmount,
            // Customer order fields
            String deliveryAddress,
            String deliveryCity,
            String deliveryPostalCode,
            String deliveryPhone,
            String paymentMethodDetails,
            String orderStatus,
            String paymentStatus,
            String trackingNumber,
            LocalDate estimatedDeliveryDate
    ) throws SQLException {

        String billNumber = generateBillNumber();

        System.out.println("🔍 DEBUG createBill:");
        System.out.println("   Bill Number: " + billNumber);
        System.out.println("   User ID: '" + userId + "'");
        System.out.println("   User Type: " + userType);
        System.out.println("   Channel: " + channel);

        String sql;

        // Different SQL based on channel
        if ("ONLINE".equals(channel)) {
            // Online order - populate customer fields
            sql = "INSERT INTO bills " +
                    "(bill_number, customer_id, channel, subtotal, discount_amount, " +
                    "total_amount, amount_paid, change_amount, " +
                    "delivery_address, delivery_city, delivery_postal_code, delivery_phone, " +
                    "payment_method_details, order_status, payment_status, " +
                    "tracking_number, estimated_delivery_date, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        } else {
            // In-store - populate cashier fields (backward compatible)
            sql = "INSERT INTO bills (bill_number, cashier_id, channel, subtotal, discount_amount, " +
                    "total_amount, amount_paid, change_amount, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        }

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);
            stmt.setString(2, userId);
            stmt.setString(3, channel);
            stmt.setBigDecimal(4, subtotal);
            stmt.setBigDecimal(5, discountAmount);
            stmt.setBigDecimal(6, totalAmount);
            stmt.setBigDecimal(7, amountPaid);
            stmt.setBigDecimal(8, changeAmount);

            // If online order, set additional fields
            if ("ONLINE".equals(channel)) {
                stmt.setString(9, deliveryAddress);
                stmt.setString(10, deliveryCity);
                stmt.setString(11, deliveryPostalCode);
                stmt.setString(12, deliveryPhone);
                // Store payment method in payment_method_details as JSON-like string
                String paymentDetails = paymentMethodDetails != null ? paymentMethodDetails :
                        "{\"method\":\"" + paymentMethod + "\"}";
                stmt.setString(13, paymentDetails);
                stmt.setString(14, orderStatus);
                stmt.setString(15, paymentStatus);
                stmt.setString(16, trackingNumber);
                stmt.setDate(17, estimatedDeliveryDate != null ? Date.valueOf(estimatedDeliveryDate) : null);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Bill created: " + billNumber + " (" + channel + ")");
                return billNumber;
            }

            throw new SQLException("Failed to create bill");
        }
    }

    /**
     * 🆕 OLD METHOD - For backward compatibility with existing cashier code
     * @deprecated Use the new createBill method with all parameters
     */
    @Deprecated
    public String createBill(String cashierId, String channel, BigDecimal subtotal,
                             BigDecimal discountAmount, BigDecimal totalAmount,
                             BigDecimal amountPaid, BigDecimal changeAmount) throws SQLException {

        return createBill(
                cashierId,
                "CASHIER",
                channel != null ? channel : "IN_STORE",
                "CASH",
                subtotal,
                discountAmount,
                totalAmount,
                amountPaid,
                changeAmount,
                null, null, null, null, null, null, null, null, null
        );
    }

    /**
     * Add item to bill
     */
    public void addBillItem(String billNumber, String productCode, int quantity,
                            BigDecimal priceAtSale, BigDecimal discountApplied) throws SQLException {
        String sql = "INSERT INTO bill_items (bill_number, product_code, quantity, price_at_sale, discount_applied) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);
            stmt.setString(2, productCode);
            stmt.setInt(3, quantity);
            stmt.setBigDecimal(4, priceAtSale);
            stmt.setBigDecimal(5, discountApplied);

            stmt.executeUpdate();
        }
    }

    /**
     * 🆕 NEW - Get customer orders
     */
    public List<BillDTO> getOrdersByCustomer(String customerId) throws SQLException {
        List<BillDTO> orders = new ArrayList<>();

        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, b.customer_id, " +
                "b.channel, b.subtotal, b.discount_amount, b.amount_paid, b.change_amount, " +
                "b.order_status, b.payment_status, b.tracking_number, b.estimated_delivery_date, " +
                "b.delivery_address, b.delivery_city, b.delivery_postal_code, b.delivery_phone " +
                "FROM bills b " +
                "WHERE b.customer_id = ? AND b.channel = 'ONLINE' " +
                "ORDER BY b.transaction_date DESC";

        System.out.println("🔍 DEBUG getOrdersByCustomer:");
        System.out.println("   Searching for customer_id: '" + customerId + "'");
        System.out.println("   SQL: " + sql);

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    BillDTO order = new BillDTO();
                    order.setBillNumber(rs.getString("bill_number"));
                    order.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setUserId(rs.getString("customer_id"));
                    order.setChannel(rs.getString("channel"));
                    order.setSubtotal(rs.getBigDecimal("subtotal"));
                    order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    order.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    order.setChangeAmount(rs.getBigDecimal("change_amount"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setTrackingNumber(rs.getString("tracking_number"));

                    Date estimatedDate = rs.getDate("estimated_delivery_date");
                    if (estimatedDate != null) {
                        order.setEstimatedDeliveryDate(estimatedDate.toLocalDate());
                    }

                    orders.add(order);

                    System.out.println("   ✅ Found order: " + order.getBillNumber() +
                                     " | Status: " + order.getOrderStatus() +
                                     " | Customer: " + order.getUserId());
                }

                System.out.println("   📊 Total orders found: " + count);
            }
        }

        return orders;
    }

    /**
     * 🆕 NEW - Cancel customer order
     */
    public boolean cancelOrder(String billNumber, String customerId) throws SQLException {
        String sql = "UPDATE bills SET order_status = 'CANCELLED' " +
                "WHERE bill_number = ? AND customer_id = ? AND order_status IN ('PENDING', 'PROCESSING')";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);
            stmt.setString(2, customerId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🚫 Order cancelled: " + billNumber);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * 🆕 NEW - Update order status
     */
    public boolean updateOrderStatus(String billNumber, String newStatus) throws SQLException {
        String sql = "UPDATE bills SET order_status = ? WHERE bill_number = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, billNumber);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("📦 Order status updated: " + billNumber + " -> " + newStatus);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * 🆕 NEW - Get single order by customer (with full details including items)
     */
    public BillDTO getOrderByCustomer(String billNumber, String customerId) throws SQLException {
        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, b.customer_id, " +
                "b.channel, b.subtotal, b.discount_amount, b.amount_paid, b.change_amount, " +
                "b.payment_method_details, " +
                "b.order_status, b.payment_status, b.tracking_number, b.estimated_delivery_date, " +
                "b.delivery_address, b.delivery_city, b.delivery_postal_code, b.delivery_phone " +
                "FROM bills b " +
                "WHERE b.bill_number = ? AND b.customer_id = ? AND b.channel = 'ONLINE'";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);
            stmt.setString(2, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BillDTO order = new BillDTO();
                    order.setBillNumber(rs.getString("bill_number"));
                    order.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setUserId(rs.getString("customer_id"));
                    order.setChannel(rs.getString("channel"));
                    order.setSubtotal(rs.getBigDecimal("subtotal"));
                    order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    order.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    order.setChangeAmount(rs.getBigDecimal("change_amount"));

                    // Extract payment method from payment_method_details JSON
                    String paymentDetails = rs.getString("payment_method_details");
                    if (paymentDetails != null) {
                        order.setPaymentMethod(extractPaymentMethod(paymentDetails));
                    }

                    order.setOrderStatus(rs.getString("order_status"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setTrackingNumber(rs.getString("tracking_number"));

                    Date estimatedDate = rs.getDate("estimated_delivery_date");
                    if (estimatedDate != null) {
                        order.setEstimatedDeliveryDate(estimatedDate.toLocalDate());
                    }

                    order.setDeliveryAddress(rs.getString("delivery_address"));
                    order.setDeliveryCity(rs.getString("delivery_city"));
                    order.setDeliveryPostalCode(rs.getString("delivery_postal_code"));
                    order.setDeliveryPhone(rs.getString("delivery_phone"));

                    // Get items
                    order.setItems(getBillItems(billNumber));

                    return order;
                }
            }
        }

        return null;
    }

    /**
     * Helper method to extract payment method from payment_method_details JSON
     */
    private String extractPaymentMethod(String paymentDetails) {
        if (paymentDetails == null || paymentDetails.trim().isEmpty()) {
            return "UNKNOWN";
        }
        // Simple JSON parsing for {"method":"CREDIT_CARD"}
        if (paymentDetails.contains("\"method\"")) {
            int start = paymentDetails.indexOf("\"method\":\"") + 10;
            int end = paymentDetails.indexOf("\"", start);
            if (start > 9 && end > start) {
                return paymentDetails.substring(start, end);
            }
        }
        return "UNKNOWN";
    }

    /**
     * Get all bills (for manager)
     */
    public List<BillDTO> getAllBills() throws SQLException {
        List<BillDTO> bills = new ArrayList<>();

        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, " +
                "COALESCE(b.cashier_id, b.customer_id) as user_id, " +
                "u.full_name, b.channel, b.subtotal, b.discount_amount, " +
                "b.amount_paid, b.change_amount " +
                "FROM bills b " +
                "LEFT JOIN users u ON (b.cashier_id = u.user_id OR b.customer_id = u.user_id) " +
                "ORDER BY b.transaction_date DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BillDTO bill = new BillDTO();
                bill.setBillNumber(rs.getString("bill_number"));
                bill.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                bill.setUserId(rs.getString("user_id"));
                bill.setCashierName(rs.getString("full_name"));
                bill.setChannel(rs.getString("channel"));
                bill.setSubtotal(rs.getBigDecimal("subtotal"));
                bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                bill.setAmountPaid(rs.getBigDecimal("amount_paid"));
                bill.setChangeAmount(rs.getBigDecimal("change_amount"));
                bills.add(bill);
            }
        }

        return bills;
    }

    /**
     * Get bills by cashier
     */
    public List<BillDTO> getBillsByCashier(String cashierId) throws SQLException {
        List<BillDTO> bills = new ArrayList<>();

        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, b.cashier_id, " +
                "u.full_name, b.channel, b.subtotal, b.discount_amount, " +
                "b.amount_paid, b.change_amount " +
                "FROM bills b " +
                "LEFT JOIN users u ON b.cashier_id = u.user_id " +
                "WHERE b.cashier_id = ? " +
                "ORDER BY b.transaction_date DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cashierId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BillDTO bill = new BillDTO();
                    bill.setBillNumber(rs.getString("bill_number"));
                    bill.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                    bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                    bill.setUserId(rs.getString("cashier_id"));
                    bill.setCashierName(rs.getString("full_name"));
                    bill.setChannel(rs.getString("channel"));
                    bill.setSubtotal(rs.getBigDecimal("subtotal"));
                    bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    bill.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    bill.setChangeAmount(rs.getBigDecimal("change_amount"));
                    bills.add(bill);
                }
            }
        }

        return bills;
    }

    /**
     * Get single bill with items
     */
    public BillDTO getBillByNumber(String billNumber) throws SQLException {
        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, " +
                "COALESCE(b.cashier_id, b.customer_id) as user_id, " +
                "u.full_name, b.channel, b.subtotal, b.discount_amount, " +
                "b.amount_paid, b.change_amount, " +
                "b.order_status, b.payment_status, b.tracking_number, b.estimated_delivery_date " +
                "FROM bills b " +
                "LEFT JOIN users u ON (b.cashier_id = u.user_id OR b.customer_id = u.user_id) " +
                "WHERE b.bill_number = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BillDTO bill = new BillDTO();
                    bill.setBillNumber(rs.getString("bill_number"));
                    bill.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                    bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                    bill.setUserId(rs.getString("user_id"));
                    bill.setCashierName(rs.getString("full_name"));
                    bill.setChannel(rs.getString("channel"));
                    bill.setSubtotal(rs.getBigDecimal("subtotal"));
                    bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    bill.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    bill.setChangeAmount(rs.getBigDecimal("change_amount"));
                    bill.setOrderStatus(rs.getString("order_status"));
                    bill.setPaymentStatus(rs.getString("payment_status"));
                    bill.setTrackingNumber(rs.getString("tracking_number"));

                    Date estimatedDate = rs.getDate("estimated_delivery_date");
                    if (estimatedDate != null) {
                        bill.setEstimatedDeliveryDate(estimatedDate.toLocalDate());
                    }

                    bill.setItems(getBillItems(billNumber));
                    return bill;
                }
            }
        }

        return null;
    }

    /**
     * Get items for a bill
     */
    public List<BillItemDTO> getBillItems(String billNumber) throws SQLException {
        List<BillItemDTO> items = new ArrayList<>();

        String sql = "SELECT bi.bill_item_id, bi.product_code, p.name, bi.quantity, " +
                "bi.price_at_sale, bi.discount_applied " +
                "FROM bill_items bi " +
                "LEFT JOIN products p ON bi.product_code = p.product_code " +
                "WHERE bi.bill_number = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BigDecimal priceAtSale = rs.getBigDecimal("price_at_sale");
                    BigDecimal discountApplied = rs.getBigDecimal("discount_applied");
                    int quantity = rs.getInt("quantity");

                    BigDecimal itemTotal = priceAtSale.multiply(BigDecimal.valueOf(quantity))
                            .subtract(discountApplied);

                    items.add(new BillItemDTO(
                            rs.getLong("bill_item_id"),
                            rs.getString("product_code"),
                            rs.getString("name"),
                            quantity,
                            priceAtSale,
                            itemTotal
                    ));
                }
            }
        }

        return items;
    }

    /**
     * Call stored procedure to deduct stock (backward compatible - defaults to SHELF)
     */
    public int deductStockForSale(String productCode, int quantity, String billNumber, String userId) throws SQLException {
        return deductStockForSale(productCode, quantity, billNumber, userId, "SHELF");
    }

    /**
     * 🆕 Call stored procedure to deduct stock from specific location (SHELF or WEBSITE)
     */
    public int deductStockForSale(String productCode, int quantity, String billNumber, String userId, String location) throws SQLException {
        String sql = "{CALL deduct_stock_for_sale_v2(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = Db.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, productCode);
            stmt.setInt(2, quantity);
            stmt.setString(3, location);  // SHELF or WEBSITE
            stmt.setString(4, billNumber);
            stmt.setString(5, userId);
            stmt.registerOutParameter(6, java.sql.Types.INTEGER);

            stmt.execute();

            int batchIdUsed = stmt.getInt(6);

            return batchIdUsed;

        } catch (SQLException e) {
            System.err.println("Error calling deduct_stock_for_sale_v2: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 🆕 Get all customer orders (for admin dashboard)
     */
    public List<BillDTO> getAllCustomerOrders() throws SQLException {
        List<BillDTO> orders = new ArrayList<>();

        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, b.customer_id, " +
                "b.channel, b.subtotal, b.discount_amount, b.amount_paid, b.change_amount, " +
                "b.order_status, b.payment_status, b.tracking_number, b.estimated_delivery_date, " +
                "b.delivery_address, b.delivery_city, b.delivery_postal_code, b.delivery_phone " +
                "FROM bills b " +
                "WHERE b.channel = 'ONLINE' " +
                "ORDER BY b.transaction_date DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BillDTO order = new BillDTO();
                order.setBillNumber(rs.getString("bill_number"));
                order.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setUserId(rs.getString("customer_id"));
                order.setChannel(rs.getString("channel"));
                order.setSubtotal(rs.getBigDecimal("subtotal"));
                order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                order.setAmountPaid(rs.getBigDecimal("amount_paid"));
                order.setChangeAmount(rs.getBigDecimal("change_amount"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setTrackingNumber(rs.getString("tracking_number"));

                Date estimatedDate = rs.getDate("estimated_delivery_date");
                if (estimatedDate != null) {
                    order.setEstimatedDeliveryDate(estimatedDate.toLocalDate());
                }

                order.setDeliveryAddress(rs.getString("delivery_address"));
                order.setDeliveryCity(rs.getString("delivery_city"));
                order.setDeliveryPostalCode(rs.getString("delivery_postal_code"));
                order.setDeliveryPhone(rs.getString("delivery_phone"));

                orders.add(order);
            }
        }

        System.out.println("📊 Admin: Retrieved " + orders.size() + " customer orders");
        return orders;
    }

    /**
     * 🆕 Approve payment (change payment_status from PENDING to PAID)
     */
    public boolean approvePayment(String billNumber) throws SQLException {
        String sql = "UPDATE bills SET payment_status = 'PAID', order_status = 'PROCESSING' " +
                "WHERE bill_number = ? AND payment_status = 'PENDING'";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Payment approved: " + billNumber + " (PAID, PROCESSING)");
                return true;
            }

            return false;
        }
    }
}

