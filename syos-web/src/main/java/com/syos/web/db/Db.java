package com.syos.web.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class Db {

    private static HikariDataSource dataSource;

    static {
        try {
            // Force-load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded");

            // Configure HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/syos_billing_Web" +
                    "?useSSL=false" +
                    "&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=Asia/Colombo");  // ✅ FIXED!
            config.setUsername("root");
            config.setPassword("poojana3540");

            // Connection pool settings for concurrency
            config.setMaximumPoolSize(40);      // 40 connections ready
            config.setMinimumIdle(10);           // Keep 10 always open
            config.setConnectionTimeout(30000); // 30 second timeout
            config.setIdleTimeout(600000);      // 10 minutes idle timeout
            config.setMaxLifetime(1800000);     // 30 minutes max lifetime

            dataSource = new HikariDataSource(config);
            System.out.println("✅ HikariCP Connection Pool initialized: 20 connections ready (Asia/Colombo timezone)");

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize HikariCP");
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private Db() {}

    /**
     * Get a connection from the pool (FAST - already open!)
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Shutdown the connection pool (call on application shutdown)
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✅ HikariCP Connection Pool closed");
        }
    }
}