package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.db.Db;
import com.syos.web.domain.model.CustomerAddress;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Customer Address Data Access Object
 * Handles all database operations for customer addresses
 */
public class CustomerAddressDao {

    /**
     * Get all addresses for a user
     */
    public List<CustomerAddress> getUserAddresses(String userId) throws SQLException {
        List<CustomerAddress> addresses = new ArrayList<>();
        String sql = "SELECT address_id, user_id, address_label, full_name, phone, " +
                "address_line1, address_line2, city, postal_code, is_default, " +
                "created_at, updated_at " +
                "FROM customer_addresses " +
                "WHERE user_id = ? " +
                "ORDER BY is_default DESC, created_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        }
        return addresses;
    }

    /**
     * Add new address
     */
    public CustomerAddress addAddress(CustomerAddress address) throws SQLException {
        // If this is set as default, unset other defaults first
        if (address.isDefault()) {
            unsetAllDefaults(address.getUserId());
        }

        String sql = "INSERT INTO customer_addresses " +
                "(user_id, address_label, full_name, phone, address_line1, address_line2, city, postal_code, is_default) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, address.getUserId());
            stmt.setString(2, address.getAddressLabel());
            stmt.setString(3, address.getFullName());
            stmt.setString(4, address.getPhone());
            stmt.setString(5, address.getAddressLine1());
            stmt.setString(6, address.getAddressLine2());
            stmt.setString(7, address.getCity());
            stmt.setString(8, address.getPostalCode());
            stmt.setBoolean(9, address.isDefault());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        address.setAddressId(rs.getInt(1));
                        System.out.println("✅ Address added: ID=" + address.getAddressId());
                        return address;
                    }
                }
            }

            throw new SQLException("Failed to add address");
        }
    }

    /**
     * Update existing address
     */
    public boolean updateAddress(CustomerAddress address) throws SQLException {
        // If this is set as default, unset other defaults first
        if (address.isDefault()) {
            unsetAllDefaults(address.getUserId());
        }

        String sql = "UPDATE customer_addresses SET " +
                "address_label = ?, full_name = ?, phone = ?, " +
                "address_line1 = ?, address_line2 = ?, city = ?, postal_code = ?, " +
                "is_default = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE address_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address.getAddressLabel());
            stmt.setString(2, address.getFullName());
            stmt.setString(3, address.getPhone());
            stmt.setString(4, address.getAddressLine1());
            stmt.setString(5, address.getAddressLine2());
            stmt.setString(6, address.getCity());
            stmt.setString(7, address.getPostalCode());
            stmt.setBoolean(8, address.isDefault());
            stmt.setInt(9, address.getAddressId());
            stmt.setString(10, address.getUserId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Address updated: ID=" + address.getAddressId());
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Delete address
     */
    public boolean deleteAddress(Integer addressId, String userId) throws SQLException {
        String sql = "DELETE FROM customer_addresses WHERE address_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addressId);
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ Address deleted: ID=" + addressId);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Set address as default
     */
    public boolean setDefaultAddress(Integer addressId, String userId) throws SQLException {
        // First unset all defaults
        unsetAllDefaults(userId);

        // Then set this one as default
        String sql = "UPDATE customer_addresses SET is_default = TRUE, updated_at = CURRENT_TIMESTAMP " +
                "WHERE address_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addressId);
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Default address set: ID=" + addressId);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Get default address for a user
     */
    public Optional<CustomerAddress> getDefaultAddress(String userId) throws SQLException {
        String sql = "SELECT address_id, user_id, address_label, full_name, phone, " +
                "address_line1, address_line2, city, postal_code, is_default, " +
                "created_at, updated_at " +
                "FROM customer_addresses " +
                "WHERE user_id = ? AND is_default = TRUE " +
                "LIMIT 1";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAddress(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find address by ID
     */
    public Optional<CustomerAddress> findById(Integer addressId, String userId) throws SQLException {
        String sql = "SELECT address_id, user_id, address_label, full_name, phone, " +
                "address_line1, address_line2, city, postal_code, is_default, " +
                "created_at, updated_at " +
                "FROM customer_addresses " +
                "WHERE address_id = ? AND user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addressId);
            stmt.setString(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAddress(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Unset all default addresses for a user
     */
    private void unsetAllDefaults(String userId) throws SQLException {
        String sql = "UPDATE customer_addresses SET is_default = FALSE WHERE user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to CustomerAddress entity
     */
    private CustomerAddress mapResultSetToAddress(ResultSet rs) throws SQLException {
        Integer addressId = rs.getInt("address_id");
        String userId = rs.getString("user_id");
        String addressLabel = rs.getString("address_label");
        String fullName = rs.getString("full_name");
        String phone = rs.getString("phone");
        String addressLine1 = rs.getString("address_line1");
        String addressLine2 = rs.getString("address_line2");
        String city = rs.getString("city");
        String postalCode = rs.getString("postal_code");
        boolean isDefault = rs.getBoolean("is_default");

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new CustomerAddress(
                addressId,
                userId,
                addressLabel,
                fullName,
                phone,
                addressLine1,
                addressLine2,
                city,
                postalCode,
                isDefault,
                createdAt != null ? createdAt.toLocalDateTime() : null,
                updatedAt != null ? updatedAt.toLocalDateTime() : null
        );
    }
}