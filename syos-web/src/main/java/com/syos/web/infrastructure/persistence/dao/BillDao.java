package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.BillItemDTO;
import com.syos.web.db.Db;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Bills
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
     * Create a new bill
     * Returns the bill number
     */
    public String createBill(String cashierId, String channel, BigDecimal subtotal,
                             BigDecimal discountAmount, BigDecimal totalAmount,
                             BigDecimal amountPaid, BigDecimal changeAmount) throws SQLException {

        String billNumber = generateBillNumber();

        // 🆕 UPDATED: Added amount_paid and change_amount
        String sql = "INSERT INTO bills (bill_number, transaction_date, cashier_id, channel, " +
                "subtotal, discount_amount, total_amount, amount_paid, change_amount) " +
                "VALUES (?, NOW(), ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);
            stmt.setString(2, cashierId);
            stmt.setString(3, channel);
            stmt.setBigDecimal(4, subtotal);
            stmt.setBigDecimal(5, discountAmount);
            stmt.setBigDecimal(6, totalAmount);
            stmt.setBigDecimal(7, amountPaid);      // 🆕 NEW
            stmt.setBigDecimal(8, changeAmount);    // 🆕 NEW

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return billNumber;
            }

            throw new SQLException("Failed to create bill");
        }
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
     * Get all bills (for manager)
     */
    public List<BillDTO> getAllBills() throws SQLException {
        List<BillDTO> bills = new ArrayList<>();

        // 🆕 UPDATED: Added amount_paid and change_amount to SELECT
        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, b.cashier_id, " +
                "u.full_name, b.channel, b.subtotal, b.discount_amount, " +
                "b.amount_paid, b.change_amount " +
                "FROM bills b " +
                "LEFT JOIN users u ON b.cashier_id = u.user_id " +
                "ORDER BY b.transaction_date DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BillDTO bill = new BillDTO();
                bill.setBillNumber(rs.getString("bill_number"));
                bill.setBillDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                bill.setUserId(rs.getString("cashier_id"));
                bill.setCashierName(rs.getString("full_name"));
                bill.setPaymentMethod(rs.getString("channel"));
                bill.setSubtotal(rs.getBigDecimal("subtotal"));
                bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                bill.setAmountPaid(rs.getBigDecimal("amount_paid"));        // 🆕 NEW
                bill.setChangeAmount(rs.getBigDecimal("change_amount"));    // 🆕 NEW
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

        // 🆕 UPDATED: Added amount_paid and change_amount to SELECT
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
                    bill.setPaymentMethod(rs.getString("channel"));
                    bill.setSubtotal(rs.getBigDecimal("subtotal"));
                    bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    bill.setAmountPaid(rs.getBigDecimal("amount_paid"));        // 🆕 NEW
                    bill.setChangeAmount(rs.getBigDecimal("change_amount"));    // 🆕 NEW
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
        // 🆕 UPDATED: Added amount_paid and change_amount to SELECT
        String sql = "SELECT b.bill_number, b.transaction_date, b.total_amount, b.cashier_id, " +
                "u.full_name, b.channel, b.subtotal, b.discount_amount, " +
                "b.amount_paid, b.change_amount " +
                "FROM bills b " +
                "LEFT JOIN users u ON b.cashier_id = u.user_id " +
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
                    bill.setUserId(rs.getString("cashier_id"));
                    bill.setCashierName(rs.getString("full_name"));
                    bill.setPaymentMethod(rs.getString("channel"));
                    bill.setSubtotal(rs.getBigDecimal("subtotal"));
                    bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    bill.setAmountPaid(rs.getBigDecimal("amount_paid"));        // 🆕 NEW
                    bill.setChangeAmount(rs.getBigDecimal("change_amount"));    // 🆕 NEW
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
     * Call stored procedure to deduct stock
     */
    public int deductStockForSale(String productCode, int quantity, String billNumber, String userId) throws SQLException {
        String sql = "{CALL deduct_stock_for_sale(?, ?, ?, ?, ?)}";

        try (Connection conn = Db.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, productCode);
            stmt.setInt(2, quantity);
            stmt.setString(3, billNumber);
            stmt.setString(4, userId);
            stmt.registerOutParameter(5, java.sql.Types.INTEGER);

            stmt.execute();

            int batchIdUsed = stmt.getInt(5);

            return batchIdUsed;

        } catch (SQLException e) {
            System.err.println("Error calling deduct_stock_for_sale: " + e.getMessage());
            throw e;
        }
    }
}