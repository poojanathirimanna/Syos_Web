package com.syos.web.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syos_billing"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASS = "poojana3540";

    private Db() {}

    public static Connection getConnection() throws SQLException {

        try {
            // Force-load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver NOT FOUND");
            throw new SQLException("MySQL JDBC Driver not found in runtime classpath", e);
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
