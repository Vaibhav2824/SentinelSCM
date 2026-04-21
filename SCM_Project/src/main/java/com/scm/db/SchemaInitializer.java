package com.scm.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * SchemaInitializer - creates all MySQL tables on startup.
 * Called once during application startup after DatabaseManager is ready.
 *
 * GRASP: Creator - responsible for database structure creation
 * SOLID: SRP - single responsibility: schema setup only
 */
public class SchemaInitializer {

    public static void initialize() {
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {

            // Users table - maps to abstract User class + 5 subclasses (Inheritance)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id       INT AUTO_INCREMENT PRIMARY KEY,
                    name          VARCHAR(100) NOT NULL,
                    email         VARCHAR(150) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    role          ENUM('ADMIN','PROCUREMENT_MANAGER','RISK_ANALYST','WAREHOUSE_MANAGER','VENDOR') NOT NULL,
                    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Vendors table - core entity (Composition root)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vendors (
                    vendor_id  INT AUTO_INCREMENT PRIMARY KEY,
                    name       VARCHAR(150) NOT NULL,
                    contact    VARCHAR(200),
                    rating     DECIMAL(3,2) DEFAULT 0.00,
                    risk_score DECIMAL(4,3) DEFAULT 0.000,
                    status     ENUM('ACTIVE','PENDING','HIGH_RISK','SUSPENDED','BLACKLISTED','INACTIVE') DEFAULT 'PENDING',
                    user_id    INT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
                )
            """);

            // Evaluation criteria - Strategy pattern inputs
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS evaluation_criteria (
                    criteria_id         INT AUTO_INCREMENT PRIMARY KEY,
                    vendor_id           INT NOT NULL,
                    delivery_timeliness DECIMAL(4,3) DEFAULT 1.000,
                    defect_rate         DECIMAL(4,3) DEFAULT 0.000,
                    compliance_score    DECIMAL(4,3) DEFAULT 1.000,
                    evaluated_date      DATE,
                    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
                )
            """);

            // Purchase orders
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS purchase_orders (
                    po_id     INT AUTO_INCREMENT PRIMARY KEY,
                    vendor_id INT NOT NULL,
                    quantity  INT DEFAULT 0,
                    date      DATE,
                    status    ENUM('PENDING','DELIVERED','DELAYED','CANCELLED') DEFAULT 'PENDING',
                    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
                )
            """);

            // Risk scores - historical tracking
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS risk_scores (
                    score_id        INT AUTO_INCREMENT PRIMARY KEY,
                    vendor_id       INT NOT NULL,
                    score           DECIMAL(5,4) NOT NULL,
                    calculated_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
                )
            """);

            // Alerts - Factory + Observer pattern output
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS alerts (
                    alert_id  INT AUTO_INCREMENT PRIMARY KEY,
                    vendor_id INT NOT NULL,
                    message   TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    severity  ENUM('LOW','MEDIUM','HIGH') DEFAULT 'MEDIUM',
                    resolved  TINYINT(1) DEFAULT 0,
                    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
                )
            """);

            // Recommendations - Pure Fabrication pattern
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS recommendations (
                    rec_id              INT AUTO_INCREMENT PRIMARY KEY,
                    alert_id            INT NOT NULL,
                    suggested_vendor_id INT NOT NULL,
                    reason              TEXT,
                    FOREIGN KEY (alert_id) REFERENCES alerts(alert_id) ON DELETE CASCADE,
                    FOREIGN KEY (suggested_vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
                )
            """);

            // Inventory
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS inventory (
                    item_id      INT AUTO_INCREMENT PRIMARY KEY,
                    item_name    VARCHAR(200) NOT NULL,
                    quantity     INT DEFAULT 0,
                    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);

            // Risk rules - Protected Variations (threshold is configurable)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS risk_rules (
                    rule_id        INT AUTO_INCREMENT PRIMARY KEY,
                    threshold      DECIMAL(4,3) DEFAULT 0.700,
                    condition_text VARCHAR(200) DEFAULT 'score > threshold',
                    created_by     INT NULL,
                    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
                )
            """);

            System.out.println("[SchemaInitializer] All tables created/verified successfully.");

        } catch (SQLException e) {
            System.err.println("[SchemaInitializer] Error creating tables: " + e.getMessage());
            throw new RuntimeException("Schema initialization failed", e);
        }
    }
}
