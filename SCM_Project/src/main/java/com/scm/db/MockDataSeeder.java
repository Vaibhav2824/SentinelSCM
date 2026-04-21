package com.scm.db;

import com.scm.util.PasswordUtil;
import java.sql.*;

/**
 * MockDataSeeder - seeds the MySQL database with realistic enterprise demo data.
 * Called on startup only if tables are empty (idempotent).
 *
 * GRASP: Creator - responsible for test data creation
 * SOLID: SRP - only handles data seeding
 */
public class MockDataSeeder {

    public static void seed() {
        Connection conn = DatabaseManager.getInstance().getConnection();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("[MockDataSeeder] Data already exists. Skipping seed.");
                return;
            }
            System.out.println("[MockDataSeeder] Seeding production-like demo data...");

            // ===== USERS =====
            String insertUser = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                seedUser(ps, "System Admin",    "admin@scm.com",    "admin123",    "ADMIN");
                seedUser(ps, "Priya Sharma",    "pm@scm.com",       "pm123",       "PROCUREMENT_MANAGER");
                seedUser(ps, "Arjun Mehta",     "analyst@scm.com",  "analyst123",  "RISK_ANALYST");
                seedUser(ps, "Rajesh Kumar",    "wm@scm.com",       "wm123",       "WAREHOUSE_MANAGER");
                seedUser(ps, "Vendor Portal",   "vendor@scm.com",   "vendor123",   "VENDOR");
            }

            // ===== VENDORS (12 realistic vendors) =====
            String insertVendor = "INSERT INTO vendors (name, contact, rating, risk_score, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertVendor)) {
                seedVendor(ps, "QuickParts Ltd",       "procurement@quickparts.in",  4.50, 0.120, "ACTIVE");
                seedVendor(ps, "FastSupply Co",        "orders@fastsupply.com",       2.10, 0.780, "HIGH_RISK");
                seedVendor(ps, "ReliableGoods Intl",   "sales@reliablegoods.com",     4.90, 0.040, "ACTIVE");
                seedVendor(ps, "BudgetMaterials Inc",  "info@budgetmaterials.in",     3.20, 0.520, "ACTIVE");
                seedVendor(ps, "GlobalProcure Inc",    "sourcing@globalprocure.com",  1.50, 0.850, "HIGH_RISK");
                seedVendor(ps, "MetalWorks India",     "support@metalworks.in",       4.20, 0.180, "ACTIVE");
                seedVendor(ps, "EcoPlast Solutions",   "hello@ecoplast.co",           3.80, 0.310, "PENDING");
                seedVendor(ps, "TechComponents AG",    "sales@techcomp.de",           4.00, 0.220, "ACTIVE");
                seedVendor(ps, "Micro-Chip Logistics", "ops@microchip-logi.com",      1.80, 0.820, "HIGH_RISK");
                seedVendor(ps, "Prime Materials Co",   "prime@primematerials.com",    4.60, 0.090, "ACTIVE");
                seedVendor(ps, "NexusBuild Supplies",  "contact@nexusbuild.in",       3.50, 0.440, "ACTIVE");
                seedVendor(ps, "SteelForge Partners",  "forge@steelforge.com",        4.10, 0.160, "ACTIVE");
            }

            // ===== EVALUATION CRITERIA =====
            String insertCriteria = "INSERT INTO evaluation_criteria (vendor_id, delivery_timeliness, defect_rate, compliance_score, evaluated_date) VALUES (?, ?, ?, ?, DATE_SUB(CURDATE(), INTERVAL ? DAY))";
            try (PreparedStatement ps = conn.prepareStatement(insertCriteria)) {
                seedCriteria(ps, 1,  0.88, 0.08, 0.92, 45);
                seedCriteria(ps, 1,  0.92, 0.05, 0.95, 15);
                seedCriteria(ps, 2,  0.50, 0.30, 0.65, 40);
                seedCriteria(ps, 2,  0.45, 0.35, 0.60, 10);
                seedCriteria(ps, 3,  0.97, 0.02, 0.98, 30);
                seedCriteria(ps, 3,  0.98, 0.01, 0.99,  5);
                seedCriteria(ps, 4,  0.72, 0.18, 0.78, 25);
                seedCriteria(ps, 4,  0.70, 0.20, 0.75,  8);
                seedCriteria(ps, 5,  0.35, 0.40, 0.55, 35);
                seedCriteria(ps, 5,  0.30, 0.45, 0.50,  7);
                seedCriteria(ps, 6,  0.90, 0.06, 0.93, 20);
                seedCriteria(ps, 6,  0.88, 0.07, 0.90,  3);
                seedCriteria(ps, 7,  0.82, 0.12, 0.85, 14);
                seedCriteria(ps, 8,  0.87, 0.09, 0.91, 12);
                seedCriteria(ps, 8,  0.89, 0.07, 0.93,  2);
                seedCriteria(ps, 9,  0.28, 0.48, 0.45,  9);
                seedCriteria(ps, 10, 0.96, 0.02, 0.97,  6);
                seedCriteria(ps, 11, 0.75, 0.15, 0.80, 11);
                seedCriteria(ps, 12, 0.91, 0.05, 0.94,  4);
            }

            // ===== PURCHASE ORDERS (35 orders) =====
            String insertPO = "INSERT INTO purchase_orders (vendor_id, quantity, date, status) VALUES (?, ?, DATE_SUB(CURDATE(), INTERVAL ? DAY), ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPO)) {
                // QuickParts
                seedPO(ps, 1, 500, 60, "DELIVERED"); seedPO(ps, 1, 750, 30, "DELIVERED");
                seedPO(ps, 1, 300, 15, "DELIVERED"); seedPO(ps, 1, 420,  3, "PENDING");
                // FastSupply
                seedPO(ps, 2, 200, 50, "DELAYED");  seedPO(ps, 2, 150, 25, "DELAYED");
                seedPO(ps, 2, 100, 10, "PENDING");
                // ReliableGoods
                seedPO(ps, 3, 1000, 45, "DELIVERED"); seedPO(ps, 3, 800, 20, "DELIVERED");
                seedPO(ps, 3, 1200,  5, "DELIVERED");
                // BudgetMaterials
                seedPO(ps, 4, 400, 40, "DELIVERED"); seedPO(ps, 4, 250, 18, "DELAYED");
                seedPO(ps, 4, 350,  4, "PENDING");
                // GlobalProcure
                seedPO(ps, 5, 100, 55, "CANCELLED"); seedPO(ps, 5, 350, 22, "DELAYED");
                seedPO(ps, 5,  80,  8, "CANCELLED");
                // MetalWorks
                seedPO(ps, 6, 600, 35, "DELIVERED"); seedPO(ps, 6, 450, 12, "DELIVERED");
                seedPO(ps, 6, 300,  2, "PENDING");
                // EcoPlast
                seedPO(ps, 7, 300, 28, "DELIVERED"); seedPO(ps, 7, 200,  6, "PENDING");
                // TechComponents
                seedPO(ps, 8, 550, 32, "DELIVERED"); seedPO(ps, 8, 400, 14, "DELIVERED");
                seedPO(ps, 8, 250,  1, "PENDING");
                // Micro-Chip
                seedPO(ps, 9, 1500, 20, "DELAYED"); seedPO(ps, 9, 1200,  7, "DELAYED");
                // Prime Materials
                seedPO(ps, 10, 600, 25, "DELIVERED"); seedPO(ps, 10, 450, 10, "DELIVERED");
                seedPO(ps, 10, 200,  2, "PENDING");
                // NexusBuild
                seedPO(ps, 11, 380, 30, "DELIVERED"); seedPO(ps, 11, 290, 12, "DELIVERED");
                // SteelForge
                seedPO(ps, 12, 700, 28, "DELIVERED"); seedPO(ps, 12, 500,  9, "DELIVERED");
                seedPO(ps, 12, 350,  1, "PENDING");
            }

            // ===== RISK SCORES =====
            String insertRisk = "INSERT INTO risk_scores (vendor_id, score, calculated_date) VALUES (?, ?, DATE_SUB(NOW(), INTERVAL ? DAY))";
            try (PreparedStatement ps = conn.prepareStatement(insertRisk)) {
                seedRisk(ps, 1, 0.180, 90); seedRisk(ps, 1, 0.150, 60);
                seedRisk(ps, 1, 0.140, 30); seedRisk(ps, 1, 0.120,  7);
                seedRisk(ps, 2, 0.550, 90); seedRisk(ps, 2, 0.620, 60);
                seedRisk(ps, 2, 0.720, 30); seedRisk(ps, 2, 0.780,  7);
                seedRisk(ps, 3, 0.060, 90); seedRisk(ps, 3, 0.050, 60);
                seedRisk(ps, 3, 0.050, 30); seedRisk(ps, 3, 0.040,  7);
                seedRisk(ps, 4, 0.400, 90); seedRisk(ps, 4, 0.480, 60);
                seedRisk(ps, 4, 0.550, 30); seedRisk(ps, 4, 0.520,  7);
                seedRisk(ps, 5, 0.700, 90); seedRisk(ps, 5, 0.750, 60);
                seedRisk(ps, 5, 0.800, 30); seedRisk(ps, 5, 0.850,  7);
                seedRisk(ps, 6, 0.220, 60); seedRisk(ps, 6, 0.200, 30);
                seedRisk(ps, 6, 0.180,  7);
                seedRisk(ps, 7, 0.350, 30); seedRisk(ps, 7, 0.310,  7);
                seedRisk(ps, 8, 0.250, 60); seedRisk(ps, 8, 0.220,  7);
                seedRisk(ps, 9, 0.700, 60); seedRisk(ps, 9, 0.750, 30);
                seedRisk(ps, 9, 0.820,  7);
                seedRisk(ps, 10, 0.100, 30); seedRisk(ps, 10, 0.090,  7);
                seedRisk(ps, 11, 0.480, 30); seedRisk(ps, 11, 0.440,  7);
                seedRisk(ps, 12, 0.180, 30); seedRisk(ps, 12, 0.160,  7);
            }

            // ===== ALERTS =====
            String insertAlert = "INSERT INTO alerts (vendor_id, message, severity, resolved, timestamp) VALUES (?, ?, ?, ?, DATE_SUB(NOW(), INTERVAL ? HOUR))";
            try (PreparedStatement ps = conn.prepareStatement(insertAlert)) {
                seedAlert(ps, 2, "HIGH RISK: FastSupply Co risk score (0.78) exceeds threshold (0.70). Persistent delivery delays in last 3 orders. Immediate action required.", "HIGH", 0, 48);
                seedAlert(ps, 5, "CRITICAL: GlobalProcure Inc risk score (0.85) significantly exceeds safety threshold. 2 cancelled orders in 60 days. Recommend vendor suspension.", "HIGH", 0, 24);
                seedAlert(ps, 9, "CRITICAL: Micro-Chip Logistics risk score (0.82) at critical level. Severe weather disruption at Port of Shanghai. 14,000 units at risk.", "HIGH", 0, 12);
                seedAlert(ps, 4, "WARNING: BudgetMaterials Inc risk trending upward (0.52). Defect rate increased 4% this quarter. Monitor closely.", "MEDIUM", 0, 72);
                seedAlert(ps, 11, "NOTICE: NexusBuild Supplies ISO 9001 certification expires in 30 days. Renewal required to maintain compliance.", "LOW", 0, 120);
                seedAlert(ps, 2, "RESOLVED: FastSupply Co compliance review completed. Corrective action plan accepted. Score improved to 0.62.", "MEDIUM", 1, 336);
                seedAlert(ps, 7, "NOTICE: EcoPlast Solutions is a new vendor under evaluation. Performance tracking initiated.", "LOW", 1, 480);
            }

            // ===== RECOMMENDATIONS =====
            String insertRec = "INSERT INTO recommendations (alert_id, suggested_vendor_id, reason) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertRec)) {
                seedRec(ps, 1, 3, "ReliableGoods Intl — risk score 0.04, 98% on-time delivery, ISO 9001 certified. Best-in-class replacement.");
                seedRec(ps, 1, 6, "MetalWorks India — risk score 0.18, strong local presence, competitive pricing, 4.2★ rated.");
                seedRec(ps, 1, 10, "Prime Materials Co — risk score 0.09, 96% delivery timeliness, excellent compliance record.");
                seedRec(ps, 2, 1, "QuickParts Ltd — risk score 0.12, 92% delivery timeliness, 4.5★ rated. Proven track record.");
                seedRec(ps, 2, 3, "ReliableGoods Intl — risk score 0.04, best-in-class vendor for immediate substitution.");
                seedRec(ps, 2, 8, "TechComponents AG — risk score 0.22, EU-certified, scalable capacity, stable supply chain.");
                seedRec(ps, 3, 10, "Prime Materials Co — domestic supplier, can fulfill 100% volume via expedited air freight. ETA: 36 hrs.");
                seedRec(ps, 3, 12, "SteelForge Partners — regional distributor, risk score 0.16, 91% on-time delivery.");
                seedRec(ps, 4, 1, "QuickParts Ltd — proven track record, risk score 0.12, better defect rate management.");
            }

            // ===== INVENTORY =====
            String insertInventory = "INSERT INTO inventory (item_name, quantity) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertInventory)) {
                seedInventory(ps, "Steel Bolts (M10)",            5000);
                seedInventory(ps, "Copper Wire (500m)",             120);
                seedInventory(ps, "PCB Circuit Boards",             800);
                seedInventory(ps, "Rubber Gaskets (O-ring)",       3500);
                seedInventory(ps, "Aluminum Sheets (2mm)",          200);
                seedInventory(ps, "LED Display Modules",             15);
                seedInventory(ps, "Plastic Casings (ABS)",           10);
                seedInventory(ps, "Stainless Steel Flanges",        1200);
                seedInventory(ps, "Silicon Wafers (200mm)",          45);
                seedInventory(ps, "Carbon Fiber Sheets",            280);
                seedInventory(ps, "Logic Controllers (Unit-A)",    1400);
                seedInventory(ps, "Hydraulic Seals (Pack-50)",       30);
            }

            // ===== RISK RULES =====
            conn.createStatement().execute(
                "INSERT INTO risk_rules (threshold, condition_text, created_by) VALUES (0.700, 'score > threshold -> HIGH_RISK alert + recommendations', 1)"
            );

            System.out.println("[MockDataSeeder] Production-like demo data seeded successfully!");
        } catch (SQLException e) {
            System.err.println("[MockDataSeeder] Error seeding data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedUser(PreparedStatement ps, String name, String email, String pw, String role) throws SQLException {
        ps.setString(1, name); ps.setString(2, email);
        ps.setString(3, PasswordUtil.hashPassword(pw)); ps.setString(4, role);
        ps.executeUpdate();
    }
    private static void seedVendor(PreparedStatement ps, String name, String contact, double rating, double risk, String status) throws SQLException {
        ps.setString(1, name); ps.setString(2, contact);
        ps.setDouble(3, rating); ps.setDouble(4, risk); ps.setString(5, status);
        ps.executeUpdate();
    }
    private static void seedCriteria(PreparedStatement ps, int vid, double d, double dr, double c, int days) throws SQLException {
        ps.setInt(1, vid); ps.setDouble(2, d); ps.setDouble(3, dr);
        ps.setDouble(4, c); ps.setInt(5, days); ps.executeUpdate();
    }
    private static void seedPO(PreparedStatement ps, int vid, int qty, int days, String status) throws SQLException {
        ps.setInt(1, vid); ps.setInt(2, qty); ps.setInt(3, days); ps.setString(4, status);
        ps.executeUpdate();
    }
    private static void seedRisk(PreparedStatement ps, int vid, double score, int days) throws SQLException {
        ps.setInt(1, vid); ps.setDouble(2, score); ps.setInt(3, days); ps.executeUpdate();
    }
    private static void seedAlert(PreparedStatement ps, int vid, String msg, String sev, int resolved, int hours) throws SQLException {
        ps.setInt(1, vid); ps.setString(2, msg); ps.setString(3, sev);
        ps.setInt(4, resolved); ps.setInt(5, hours); ps.executeUpdate();
    }
    private static void seedRec(PreparedStatement ps, int alertId, int suggestVid, String reason) throws SQLException {
        ps.setInt(1, alertId); ps.setInt(2, suggestVid); ps.setString(3, reason);
        ps.executeUpdate();
    }
    private static void seedInventory(PreparedStatement ps, String name, int qty) throws SQLException {
        ps.setString(1, name); ps.setInt(2, qty); ps.executeUpdate();
    }
}
