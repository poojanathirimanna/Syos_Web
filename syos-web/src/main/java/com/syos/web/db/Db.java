package com.syos.web.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syos_billing_Web" +
                    "?useSSL=false" +
                    "&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASS = "poojana3540";

    private Db() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void close() {
        // No-op for DriverManager
    }
}