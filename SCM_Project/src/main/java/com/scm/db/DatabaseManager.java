package com.scm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Singleton Pattern
 *
 * Design Pattern: Singleton
 * - Only one database connection pool instance throughout the application
 * lifetime
 * - Private constructor prevents external instantiation
 * - Static getInstance() provides global access point
 *
 * GRASP: Indirection - sits between Repository classes and raw JDBC
 * GRASP: Protected Variations - DB connection details hidden from
 * service/controller layers
 * Encapsulation: all connection details private, exposed only via
 * getConnection()
 */
public class DatabaseManager {

    // === CONFIGURE THESE FOR YOUR MySQL INSTALLATION ===
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "scm_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "22vaibhav";
    // ====================================================

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
            + "&useUnicode=true&characterEncoding=UTF-8";

    // Singleton instance - volatile for thread safety
    private static volatile DatabaseManager instance;
    private Connection connection;

    /** Private constructor — prevents direct instantiation (Singleton) */
    private DatabaseManager() {
        connect();
    }

    /**
     * Returns the singleton instance of DatabaseManager.
     * Double-checked locking for thread safety.
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    /** Open MySQL connection and ensure the database exists. */
    private void connect() {
        try {
            // First, ensure the database exists (connect without DB name)
            String rootUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            try (Connection root = DriverManager.getConnection(rootUrl, DB_USER, DB_PASSWORD);
                    Statement stmt = root.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS `" + DB_NAME
                        + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                System.out.println("[DatabaseManager] Database '" + DB_NAME + "' ensured.");
            }

            // Now connect to the actual database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("[DatabaseManager] MySQL connection established to " + DB_NAME + ".");
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Failed to connect to MySQL: " + e.getMessage());
            System.err.println(
                    "[DatabaseManager] Make sure MySQL is running and credentials in DatabaseManager.java are correct.");
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * Returns the active database connection.
     * Reconnects if the connection was closed or dropped.
     */
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                System.out.println("[DatabaseManager] Reconnecting to MySQL...");
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to validate database connection", e);
        }
        return connection;
    }

    /** Closes the database connection gracefully. */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DatabaseManager] MySQL connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Error closing connection: " + e.getMessage());
        }
    }
}
