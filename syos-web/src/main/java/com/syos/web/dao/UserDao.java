package com.syos.web.dao;

import com.syos.web.db.Db;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
