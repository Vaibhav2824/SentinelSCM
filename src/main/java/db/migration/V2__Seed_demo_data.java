package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Seeds the demo dataset (5 users, 12 vendors, evaluations, orders, scores, alerts,
 * recommendations, inventory, risk rule). Java migration so passwords are hashed with the
 * same BCrypt encoder the app authenticates with; no hashes live in source control.
 */
public class V2__Seed_demo_data extends BaseJavaMigration {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Override
    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();
        seedUsers(c);
        seedVendors(c);
        seedCriteria(c);
        seedPurchaseOrders(c);
        seedRiskScores(c);
        seedAlerts(c);
        seedRecommendations(c);
        seedInventory(c);
        try (Statement st = c.createStatement()) {
            st.execute("INSERT INTO risk_rules (threshold, condition_text, created_by) "
                + "VALUES (0.7, 'score above threshold raises HIGH_RISK alert and recommendations', 1)");
        }
    }

    private void seedUsers(Connection c) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            user(ps, "System Admin",  "admin@scm.com",   "admin123",   "ADMIN");
            user(ps, "Priya Sharma",  "pm@scm.com",      "pm123",      "PROCUREMENT_MANAGER");
            user(ps, "Arjun Mehta",   "analyst@scm.com", "analyst123", "RISK_ANALYST");
            user(ps, "Rajesh Kumar",  "wm@scm.com",      "wm123",      "WAREHOUSE_MANAGER");
            user(ps, "Vendor Portal", "vendor@scm.com",  "vendor123",  "VENDOR");
        }
    }

    private void seedVendors(Connection c) throws SQLException {
        String sql = "INSERT INTO vendors (name, contact, rating, risk_score, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            vendor(ps, "QuickParts Ltd",       "procurement@quickparts.in", 4.50, 0.120, "ACTIVE");
            vendor(ps, "FastSupply Co",        "orders@fastsupply.com",      2.10, 0.780, "HIGH_RISK");
            vendor(ps, "ReliableGoods Intl",   "sales@reliablegoods.com",    4.90, 0.040, "ACTIVE");
            vendor(ps, "BudgetMaterials Inc",  "info@budgetmaterials.in",    3.20, 0.520, "ACTIVE");
            vendor(ps, "GlobalProcure Inc",    "sourcing@globalprocure.com", 1.50, 0.850, "HIGH_RISK");
            vendor(ps, "MetalWorks India",     "support@metalworks.in",      4.20, 0.180, "ACTIVE");
            vendor(ps, "EcoPlast Solutions",   "hello@ecoplast.co",          3.80, 0.310, "PENDING");
            vendor(ps, "TechComponents AG",    "sales@techcomp.de",          4.00, 0.220, "ACTIVE");
            vendor(ps, "Micro-Chip Logistics", "ops@microchip-logi.com",     1.80, 0.820, "HIGH_RISK");
            vendor(ps, "Prime Materials Co",   "prime@primematerials.com",   4.60, 0.090, "ACTIVE");
            vendor(ps, "NexusBuild Supplies",  "contact@nexusbuild.in",      3.50, 0.440, "ACTIVE");
            vendor(ps, "SteelForge Partners",  "forge@steelforge.com",       4.10, 0.160, "ACTIVE");
        }
    }

    private void seedCriteria(Connection c) throws SQLException {
        String sql = "INSERT INTO evaluation_criteria (vendor_id, delivery_timeliness, defect_rate, compliance_score, evaluated_date) "
            + "VALUES (?, ?, ?, ?, DATE_SUB(CURDATE(), INTERVAL ? DAY))";
        double[][] rows = {
            {1, 0.88, 0.08, 0.92, 45}, {1, 0.92, 0.05, 0.95, 15},
            {2, 0.50, 0.30, 0.65, 40}, {2, 0.45, 0.35, 0.60, 10},
            {3, 0.97, 0.02, 0.98, 30}, {3, 0.98, 0.01, 0.99, 5},
            {4, 0.72, 0.18, 0.78, 25}, {4, 0.70, 0.20, 0.75, 8},
            {5, 0.35, 0.40, 0.55, 35}, {5, 0.30, 0.45, 0.50, 7},
            {6, 0.90, 0.06, 0.93, 20}, {6, 0.88, 0.07, 0.90, 3},
            {7, 0.82, 0.12, 0.85, 14},
            {8, 0.87, 0.09, 0.91, 12}, {8, 0.89, 0.07, 0.93, 2},
            {9, 0.28, 0.48, 0.45, 9},
            {10, 0.96, 0.02, 0.97, 6},
            {11, 0.75, 0.15, 0.80, 11},
            {12, 0.91, 0.05, 0.94, 4},
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (double[] r : rows) {
                ps.setInt(1, (int) r[0]); ps.setDouble(2, r[1]); ps.setDouble(3, r[2]);
                ps.setDouble(4, r[3]); ps.setInt(5, (int) r[4]); ps.executeUpdate();
            }
        }
    }

    private void seedPurchaseOrders(Connection c) throws SQLException {
        String sql = "INSERT INTO purchase_orders (vendor_id, quantity, order_date, status) "
            + "VALUES (?, ?, DATE_SUB(CURDATE(), INTERVAL ? DAY), ?)";
        Object[][] rows = {
            {1, 500, 60, "DELIVERED"}, {1, 750, 30, "DELIVERED"}, {1, 300, 15, "DELIVERED"}, {1, 420, 3, "PENDING"},
            {2, 200, 50, "DELAYED"}, {2, 150, 25, "DELAYED"}, {2, 100, 10, "PENDING"},
            {3, 1000, 45, "DELIVERED"}, {3, 800, 20, "DELIVERED"}, {3, 1200, 5, "DELIVERED"},
            {4, 400, 40, "DELIVERED"}, {4, 250, 18, "DELAYED"}, {4, 350, 4, "PENDING"},
            {5, 100, 55, "CANCELLED"}, {5, 350, 22, "DELAYED"}, {5, 80, 8, "CANCELLED"},
            {6, 600, 35, "DELIVERED"}, {6, 450, 12, "DELIVERED"}, {6, 300, 2, "PENDING"},
            {7, 300, 28, "DELIVERED"}, {7, 200, 6, "PENDING"},
            {8, 550, 32, "DELIVERED"}, {8, 400, 14, "DELIVERED"}, {8, 250, 1, "PENDING"},
            {9, 1500, 20, "DELAYED"}, {9, 1200, 7, "DELAYED"},
            {10, 600, 25, "DELIVERED"}, {10, 450, 10, "DELIVERED"}, {10, 200, 2, "PENDING"},
            {11, 380, 30, "DELIVERED"}, {11, 290, 12, "DELIVERED"},
            {12, 700, 28, "DELIVERED"}, {12, 500, 9, "DELIVERED"}, {12, 350, 1, "PENDING"},
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Object[] r : rows) {
                ps.setInt(1, (int) r[0]); ps.setInt(2, (int) r[1]); ps.setInt(3, (int) r[2]);
                ps.setString(4, (String) r[3]); ps.executeUpdate();
            }
        }
    }

    private void seedRiskScores(Connection c) throws SQLException {
        String sql = "INSERT INTO risk_scores (vendor_id, score, calculated_date) "
            + "VALUES (?, ?, DATE_SUB(NOW(6), INTERVAL ? DAY))";
        double[][] rows = {
            {1, 0.180, 90}, {1, 0.150, 60}, {1, 0.140, 30}, {1, 0.120, 7},
            {2, 0.550, 90}, {2, 0.620, 60}, {2, 0.720, 30}, {2, 0.780, 7},
            {3, 0.060, 90}, {3, 0.050, 60}, {3, 0.050, 30}, {3, 0.040, 7},
            {4, 0.400, 90}, {4, 0.480, 60}, {4, 0.550, 30}, {4, 0.520, 7},
            {5, 0.700, 90}, {5, 0.750, 60}, {5, 0.800, 30}, {5, 0.850, 7},
            {6, 0.220, 60}, {6, 0.200, 30}, {6, 0.180, 7},
            {7, 0.350, 30}, {7, 0.310, 7},
            {8, 0.250, 60}, {8, 0.220, 7},
            {9, 0.700, 60}, {9, 0.750, 30}, {9, 0.820, 7},
            {10, 0.100, 30}, {10, 0.090, 7},
            {11, 0.480, 30}, {11, 0.440, 7},
            {12, 0.180, 30}, {12, 0.160, 7},
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (double[] r : rows) {
                ps.setInt(1, (int) r[0]); ps.setDouble(2, r[1]); ps.setInt(3, (int) r[2]); ps.executeUpdate();
            }
        }
    }

    private void seedAlerts(Connection c) throws SQLException {
        String sql = "INSERT INTO alerts (vendor_id, message, severity, resolved, created_at) "
            + "VALUES (?, ?, ?, ?, DATE_SUB(NOW(6), INTERVAL ? HOUR))";
        Object[][] rows = {
            {2, "HIGH RISK: FastSupply Co risk score (0.78) exceeds threshold (0.70). Persistent delivery delays in last 3 orders. Immediate action required.", "HIGH", false, 48},
            {5, "CRITICAL: GlobalProcure Inc risk score (0.85) significantly exceeds safety threshold. 2 cancelled orders in 60 days. Recommend vendor suspension.", "HIGH", false, 24},
            {9, "CRITICAL: Micro-Chip Logistics risk score (0.82) at critical level. Severe weather disruption at Port of Shanghai. 14,000 units at risk.", "HIGH", false, 12},
            {4, "WARNING: BudgetMaterials Inc risk trending upward (0.52). Defect rate increased 4% this quarter. Monitor closely.", "MEDIUM", false, 72},
            {11, "NOTICE: NexusBuild Supplies ISO 9001 certification expires in 30 days. Renewal required to maintain compliance.", "LOW", false, 120},
            {2, "RESOLVED: FastSupply Co compliance review completed. Corrective action plan accepted. Score improved to 0.62.", "MEDIUM", true, 336},
            {7, "NOTICE: EcoPlast Solutions is a new vendor under evaluation. Performance tracking initiated.", "LOW", true, 480},
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Object[] r : rows) {
                ps.setInt(1, (int) r[0]); ps.setString(2, (String) r[1]); ps.setString(3, (String) r[2]);
                ps.setBoolean(4, (boolean) r[3]); ps.setInt(5, (int) r[4]); ps.executeUpdate();
            }
        }
    }

    private void seedRecommendations(Connection c) throws SQLException {
        String sql = "INSERT INTO recommendations (alert_id, suggested_vendor_id, reason) VALUES (?, ?, ?)";
        Object[][] rows = {
            {1, 3, "ReliableGoods Intl: risk score 0.04, 98% on-time delivery, ISO 9001 certified. Best-in-class replacement."},
            {1, 6, "MetalWorks India: risk score 0.18, strong local presence, competitive pricing, 4.2-star rated."},
            {1, 10, "Prime Materials Co: risk score 0.09, 96% delivery timeliness, excellent compliance record."},
            {2, 1, "QuickParts Ltd: risk score 0.12, 92% delivery timeliness, 4.5-star rated. Proven track record."},
            {2, 3, "ReliableGoods Intl: risk score 0.04, best-in-class vendor for immediate substitution."},
            {2, 8, "TechComponents AG: risk score 0.22, EU-certified, scalable capacity, stable supply chain."},
            {3, 10, "Prime Materials Co: domestic supplier, can fulfil 100% volume via expedited air freight. ETA 36 hrs."},
            {3, 12, "SteelForge Partners: regional distributor, risk score 0.16, 91% on-time delivery."},
            {4, 1, "QuickParts Ltd: proven track record, risk score 0.12, better defect rate management."},
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Object[] r : rows) {
                ps.setInt(1, (int) r[0]); ps.setInt(2, (int) r[1]); ps.setString(3, (String) r[2]); ps.executeUpdate();
            }
        }
    }

    private void seedInventory(Connection c) throws SQLException {
        String sql = "INSERT INTO inventory (item_name, quantity) VALUES (?, ?)";
        Object[][] rows = {
            {"Steel Bolts (M10)", 5000}, {"Copper Wire (500m)", 120}, {"PCB Circuit Boards", 800},
            {"Rubber Gaskets (O-ring)", 3500}, {"Aluminum Sheets (2mm)", 200}, {"LED Display Modules", 15},
            {"Plastic Casings (ABS)", 10}, {"Stainless Steel Flanges", 1200}, {"Silicon Wafers (200mm)", 45},
            {"Carbon Fiber Sheets", 280}, {"Logic Controllers (Unit-A)", 1400}, {"Hydraulic Seals (Pack-50)", 30},
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Object[] r : rows) {
                ps.setString(1, (String) r[0]); ps.setInt(2, (int) r[1]); ps.executeUpdate();
            }
        }
    }

    private static void user(PreparedStatement ps, String name, String email, String rawPassword, String role) throws SQLException {
        ps.setString(1, name); ps.setString(2, email);
        ps.setString(3, ENCODER.encode(rawPassword)); ps.setString(4, role);
        ps.executeUpdate();
    }

    private static void vendor(PreparedStatement ps, String name, String contact, double rating, double risk, String status) throws SQLException {
        ps.setString(1, name); ps.setString(2, contact);
        ps.setDouble(3, rating); ps.setDouble(4, risk); ps.setString(5, status);
        ps.executeUpdate();
    }
}
