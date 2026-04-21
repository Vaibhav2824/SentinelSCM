package com.scm.service;

import com.scm.db.DatabaseManager;
import com.scm.model.Role;
import com.scm.model.User;
import com.scm.model.user.*;
import com.scm.util.PasswordUtil;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AuthService - Authentication and session management.
 *
 * SOLID: SRP - single responsibility: authentication
 * GRASP: High Cohesion - focused entirely on auth business logic
 * Factory-like: createUserForRole() demonstrates Polymorphism/Inheritance
 */
public class AuthService {

    // In-memory session store: token → session data
    private static final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

    /** Authenticate user by email/password. Returns session data or null. */
    public Map<String, Object> login(String email, String password) {
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (PasswordUtil.verifyPassword(password, hash)) {
                    String token = UUID.randomUUID().toString();
                    Map<String, Object> session = new HashMap<>();
                    session.put("userId",  rs.getInt("user_id"));
                    session.put("name",    rs.getString("name"));
                    session.put("email",   rs.getString("email"));
                    session.put("role",    rs.getString("role"));
                    session.put("token",   token);
                    sessions.put(token, session);
                    System.out.println("[AuthService] Login: " + email + " (" + rs.getString("role") + ")");
                    return session;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login error: " + e.getMessage(), e);
        }
        return null;
    }

    /** Invalidate session token. */
    public boolean logout(String token) {
        return sessions.remove(token) != null;
    }

    /** Validate session token and return session data (or null). */
    public Map<String, Object> validateSession(String token) {
        return sessions.get(token);
    }

    /**
     * Create User subclass from role string.
     * Demonstrates: Inheritance + Polymorphism + Factory-like creation
     * SOLID: LSP - all subclasses substitute for User
     */
    public static User createUserForRole(String roleStr) {
        return switch (roleStr) {
            case "ADMIN"                -> new Administrator();
            case "PROCUREMENT_MANAGER"  -> new ProcurementManager();
            case "RISK_ANALYST"         -> new RiskAnalyst();
            case "WAREHOUSE_MANAGER"    -> new WarehouseManager();
            case "VENDOR"               -> new VendorUser();
            default                     -> null;
        };
    }
}
