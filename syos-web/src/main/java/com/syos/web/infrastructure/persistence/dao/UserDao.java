package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.db.Db;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    /* =========================
       LOGIN (BCrypt verify)
       ========================= */
    public boolean isValidUser(String userId, String plainPassword) {

        String sql = "SELECT password_hash FROM users WHERE user_id = ? LIMIT 1";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return false; // user not found
                }

                String storedHash = rs.getString("password_hash");
                if (storedHash == null || storedHash.isBlank()) {
                    return false;
                }

                // BCrypt check: plain password vs stored hash
                return BCrypt.checkpw(plainPassword, storedHash);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get user details by user ID (for login)
     * Returns null if user not found
     */
    public UserDetails getUserDetails(String userId) {
        String sql = "SELECT user_id, full_name, email, contact_number, role_id FROM users WHERE user_id = ? LIMIT 1";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserDetails(
                            rs.getString("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("contact_number"),
                            rs.getInt("role_id")
                    );
                }
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inner class to hold user details
     */
    public static class UserDetails {
        private final String userId;
        private final String fullName;
        private final String email;
        private final String contactNumber;
        private final int roleId;
        private final boolean isActive;  // 🆕 NEW

        // Old constructor for backward compatibility
        public UserDetails(String userId, String fullName, String email, String contactNumber, int roleId) {
            this(userId, fullName, email, contactNumber, roleId, true);
        }

        // 🆕 NEW - Full constructor with isActive
        public UserDetails(String userId, String fullName, String email, String contactNumber, int roleId, boolean isActive) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.contactNumber = contactNumber;
            this.roleId = roleId;
            this.isActive = isActive;
        }

        public String getUserId() {
            return userId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public int getRoleId() {
            return roleId;
        }

        public boolean isActive() {  // 🆕 NEW
            return isActive;
        }
    }

    /* =========================
       REGISTER – VALIDATIONS
       ========================= */

    public boolean existsByUserId(String userId) {
        String sql = "SELECT 1 FROM users WHERE user_id = ? LIMIT 1";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return true; // safer to block registration if error
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /* =========================
       REGISTER – INSERT USER (BCrypt hash)
       ========================= */

    public boolean registerUser(
            String userId,
            String fullName,
            String contactNumber,
            String email,
            String plainPassword,
            int roleId
    ) {

        // Hash password using BCrypt before saving
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        String sql = """
            INSERT INTO users
                (user_id, full_name, email, contact_number, password_hash, role_id)
            VALUES
                (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, contactNumber);
            ps.setString(5, hashedPassword);
            ps.setInt(6, roleId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* =========================
       GOOGLE SIGN-IN METHODS
       ========================= */

    /**
     * Find user by Google ID
     */
    public String findUserByGoogleId(String googleId) {
        String sql = "SELECT user_id FROM users WHERE google_id = ? LIMIT 1";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, googleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_id");
                }
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Register a new user via Google Sign-In
     * Password is null for Google users
     */
    public boolean registerGoogleUser(
            String userId,
            String fullName,
            String email,
            String googleId,
            int roleId
    ) {
        String sql = """
            INSERT INTO users
                (user_id, full_name, email, google_id, role_id)
            VALUES
                (?, ?, ?, ?, ?)
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, googleId);
            ps.setInt(5, roleId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Link Google ID to an existing user (if they signed up with email/password first)
     */
    public boolean linkGoogleId(String userId, String googleId) {
        String sql = "UPDATE users SET google_id = ? WHERE user_id = ?";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, googleId);
            ps.setString(2, userId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* =========================
       CASHIER MANAGEMENT
       ========================= */

    /**
     * Get all users with CASHIER role (role_id = 2)
     */
    public List<UserDetails> getAllCashiers() {
        List<UserDetails> cashiers = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, contact_number, role_id, is_active " +
                "FROM users WHERE role_id = 2 ORDER BY full_name";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cashiers.add(new UserDetails(
                        rs.getString("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("contact_number"),
                        rs.getInt("role_id"),
                        rs.getBoolean("is_active")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cashiers;
    }

    /**
     * Get single cashier by user_id
     */
    public UserDetails getCashierById(String userId) {
        String sql = "SELECT user_id, full_name, email, contact_number, role_id, is_active " +
                "FROM users WHERE user_id = ? AND role_id = 2 LIMIT 1";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserDetails(
                            rs.getString("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("contact_number"),
                            rs.getInt("role_id"),
                            rs.getBoolean("is_active")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new cashier (role_id = 2)
     */
    public boolean createCashier(String userId, String fullName, String email,
                                 String contactNumber, String plainPassword) {
        // Hash password
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        String sql = "INSERT INTO users (user_id, full_name, email, contact_number, password_hash, role_id, is_active) " +
                "VALUES (?, ?, ?, ?, ?, 2, TRUE)";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, contactNumber);
            ps.setString(5, hashedPassword);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update cashier details (full_name, email, contact_number)
     * Password is optional - only updated if provided
     */
    public boolean updateCashier(String userId, String fullName, String email,
                                 String contactNumber, String plainPassword) {

        String sql;

        if (plainPassword != null && !plainPassword.isEmpty()) {
            // Update with new password
            sql = "UPDATE users SET full_name = ?, email = ?, contact_number = ?, password_hash = ? " +
                    "WHERE user_id = ? AND role_id = 2";
        } else {
            // Update without password
            sql = "UPDATE users SET full_name = ?, email = ?, contact_number = ? " +
                    "WHERE user_id = ? AND role_id = 2";
        }

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (plainPassword != null && !plainPassword.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, contactNumber);
                ps.setString(4, hashedPassword);
                ps.setString(5, userId);
            } else {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, contactNumber);
                ps.setString(4, userId);
            }

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deactivate cashier (soft delete)
     */
    public boolean deactivateCashier(String userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ? AND role_id = 2";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Activate cashier
     */
    public boolean activateCashier(String userId) {
        String sql = "UPDATE users SET is_active = TRUE WHERE user_id = ? AND role_id = 2";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}