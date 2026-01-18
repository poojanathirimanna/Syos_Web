package com.syos.web.concurrency;

import com.syos.web.db.Db;
import java.sql.*;
import java.util.UUID;

/**
 * Logs all API requests for concurrency tracking and debugging
 * Helps prove that request queuing works under load
 * Thread-safe and non-blocking
 */
public class RequestLogger {

    /**
     * Log a new request to the database
     * This method is non-blocking - runs in background thread
     *
     * @param requestType Type of request (e.g., "LOGIN", "CREATE_BILL", "TRANSFER_STOCK")
     * @param userId User ID making the request (can be null for login/register)
     * @param ipAddress Client IP address
     * @return Request ID for tracking this request
     */
    public static String logRequest(String requestType, String userId, String ipAddress) {
        // Generate unique ID for this request
        String requestId = UUID.randomUUID().toString();

        // Log to database asynchronously (don't block the main request thread)
        new Thread(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = Db.getConnection();

                String sql = "INSERT INTO request_log " +
                        "(request_id, request_type, user_id, server_thread, status) " +
                        "VALUES (?, ?, ?, ?, 'PROCESSING')";

                stmt = conn.prepareStatement(sql);
                stmt.setString(1, requestId);
                stmt.setString(2, requestType);
                stmt.setString(3, userId);
                stmt.setString(4, Thread.currentThread().getName());

                stmt.executeUpdate();

                System.out.println("✓ Request logged: " + requestType +
                        " [" + requestId + "] on thread: " +
                        Thread.currentThread().getName());

            } catch (SQLException e) {
                System.err.println("❌ Failed to log request: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing resources: " + e.getMessage());
                }
            }
        }).start();

        return requestId;
    }

    /**
     * Update request status when it completes
     * This method is non-blocking - runs in background thread
     *
     * @param requestId Request ID returned from logRequest()
     * @param status Status: "COMPLETED" or "FAILED"
     * @param startTime Start time in milliseconds (from System.currentTimeMillis())
     */
    public static void updateStatus(String requestId, String status, long startTime) {
        // Calculate response time
        long responseTime = System.currentTimeMillis() - startTime;

        // Update database asynchronously
        new Thread(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = Db.getConnection();

                String sql = "UPDATE request_log SET " +
                        "status = ?, " +
                        "processing_end = CURRENT_TIMESTAMP(3), " +
                        "response_time_ms = ? " +
                        "WHERE request_id = ?";

                stmt = conn.prepareStatement(sql);
                stmt.setString(1, status);
                stmt.setLong(2, responseTime);
                stmt.setString(3, requestId);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("✓ Request " + status.toLowerCase() +
                            ": " + requestId + " (" + responseTime + "ms)");
                } else {
                    System.err.println("⚠ Request ID not found: " + requestId);
                }

            } catch (SQLException e) {
                System.err.println("❌ Failed to update request status: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing resources: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Log an error message for a request
     * This method is non-blocking - runs in background thread
     *
     * @param requestId Request ID
     * @param errorMessage Error message to log
     */
    public static void logError(String requestId, String errorMessage) {
        new Thread(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = Db.getConnection();

                String sql = "UPDATE request_log SET " +
                        "status = 'FAILED', " +
                        "error_message = ?, " +
                        "processing_end = CURRENT_TIMESTAMP(3) " +
                        "WHERE request_id = ?";

                stmt = conn.prepareStatement(sql);
                stmt.setString(1, errorMessage);
                stmt.setString(2, requestId);

                stmt.executeUpdate();

                System.out.println("✓ Error logged for request: " + requestId);

            } catch (SQLException e) {
                System.err.println("❌ Failed to log error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing resources: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Get request statistics from the last hour
     * Useful for monitoring server load
     *
     * @return Statistics string
     */
    public static String getStats() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = Db.getConnection();

            String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
                    "SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed, " +
                    "SUM(CASE WHEN status = 'PROCESSING' THEN 1 ELSE 0 END) as processing, " +
                    "AVG(response_time_ms) as avg_response_time, " +
                    "MAX(response_time_ms) as max_response_time, " +
                    "MIN(response_time_ms) as min_response_time " +
                    "FROM request_log " +
                    "WHERE request_timestamp > DATE_SUB(NOW(), INTERVAL 1 HOUR)";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return String.format(
                        "Last hour: Total=%d, Completed=%d, Failed=%d, Processing=%d, " +
                                "Avg Response=%dms, Max=%dms, Min=%dms",
                        rs.getInt("total"),
                        rs.getInt("completed"),
                        rs.getInt("failed"),
                        rs.getInt("processing"),
                        rs.getInt("avg_response_time"),
                        rs.getInt("max_response_time"),
                        rs.getInt("min_response_time")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to get stats: " + e.getMessage());
            return "Statistics unavailable: " + e.getMessage();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return "No statistics available";
    }

    /**
     * Get count of requests by status
     *
     * @param status Status to count ("COMPLETED", "FAILED", "PROCESSING")
     * @return Count of requests with that status
     */
    public static int getRequestCountByStatus(String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Db.getConnection();

            String sql = "SELECT COUNT(*) as count FROM request_log " +
                    "WHERE status = ? AND " +
                    "request_timestamp > DATE_SUB(NOW(), INTERVAL 1 HOUR)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to get count: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return 0;
    }

    /**
     * Clean up old request logs (older than 7 days)
     * Call this periodically to prevent table from growing too large
     *
     * @return Number of records deleted
     */
    public static int cleanupOldLogs() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = Db.getConnection();

            String sql = "DELETE FROM request_log " +
                    "WHERE request_timestamp < DATE_SUB(NOW(), INTERVAL 7 DAY)";

            stmt = conn.createStatement();
            int deleted = stmt.executeUpdate(sql);

            System.out.println("✓ Cleaned up " + deleted + " old request logs");
            return deleted;

        } catch (SQLException e) {
            System.err.println("❌ Failed to cleanup logs: " + e.getMessage());
            return 0;
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}