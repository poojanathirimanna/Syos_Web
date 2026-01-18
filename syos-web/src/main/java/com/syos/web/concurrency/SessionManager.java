package com.syos.web.concurrency;

import com.syos.web.db.Db;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user sessions for concurrent access
 * Tracks active sessions in memory for fast access
 * Persists session data to database for audit trail
 */
public class SessionManager {

    // In-memory cache of active sessions (thread-safe)
    private static final ConcurrentHashMap<String, SessionData> activeSessions =
            new ConcurrentHashMap<>();

    /**
     * Session data stored in memory
     */
    public static class SessionData {
        public final String userId;
        public final String roleId;
        public volatile long lastActivity;

        public SessionData(String userId, String roleId) {
            this.userId = userId;
            this.roleId = roleId;
            this.lastActivity = System.currentTimeMillis();
        }

        public void updateActivity() {
            this.lastActivity = System.currentTimeMillis();
        }

        public boolean isExpired(long timeoutMillis) {
            return System.currentTimeMillis() - lastActivity > timeoutMillis;
        }
    }

    /**
     * Create a new session for a user
     * @param userId User ID
     * @param roleId Role ID
     * @param ipAddress Client IP address
     * @param deviceInfo User agent string
     * @return Session ID
     */
    public static String createSession(String userId, String roleId,
                                       String ipAddress, String deviceInfo)
            throws SQLException {

        String sessionId = UUID.randomUUID().toString();

        // Store in database
        try (Connection conn = Db.getConnection()) {
            String sql = "INSERT INTO user_sessions " +
                    "(session_id, user_id, ip_address, device_info) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sessionId);
            stmt.setString(2, userId);
            stmt.setString(3, ipAddress);
            stmt.setString(4, deviceInfo);
            stmt.executeUpdate();
        }

        // Store in memory for fast access
        activeSessions.put(sessionId, new SessionData(userId, roleId));

        System.out.println("✓ Session created: " + sessionId + " for user: " + userId);
        return sessionId;
    }

    /**
     * Get session data
     * @param sessionId Session ID
     * @return SessionData or null if not found
     */
    public static SessionData getSession(String sessionId) {
        SessionData session = activeSessions.get(sessionId);
        if (session != null) {
            session.updateActivity();

            // Update last activity in database (async - don't wait)
            new Thread(() -> {
                try (Connection conn = Db.getConnection()) {
                    String sql = "UPDATE user_sessions SET last_activity = CURRENT_TIMESTAMP " +
                            "WHERE session_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, sessionId);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Failed to update session activity: " + e.getMessage());
                }
            }).start();
        }
        return session;
    }

    /**
     * Check if session exists and is active
     * @param sessionId Session ID
     * @return true if session is valid
     */
    public static boolean isValidSession(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }

    /**
     * Invalidate a session (logout)
     * @param sessionId Session ID
     */
    public static void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);

        // Update database (async)
        new Thread(() -> {
            try (Connection conn = Db.getConnection()) {
                String sql = "UPDATE user_sessions SET is_active = 0, " +
                        "logout_time = CURRENT_TIMESTAMP WHERE session_id = ?";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, sessionId);
                stmt.executeUpdate();

                System.out.println("✓ Session invalidated: " + sessionId);
            } catch (SQLException e) {
                System.err.println("Failed to invalidate session: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Get number of active sessions
     * @return Count of active sessions
     */
    public static int getActiveSessionCount() {
        return activeSessions.size();
    }

    /**
     * Clean up expired sessions (call this periodically)
     * @param timeoutMillis Timeout in milliseconds (e.g., 30 * 60 * 1000 = 30 minutes)
     */
    public static void cleanupExpiredSessions(long timeoutMillis) {
        activeSessions.entrySet().removeIf(entry ->
                entry.getValue().isExpired(timeoutMillis)
        );
    }

    /**
     * Get statistics for monitoring
     */
    public static String getStats() {
        return String.format("Active sessions: %d", activeSessions.size());
    }
}