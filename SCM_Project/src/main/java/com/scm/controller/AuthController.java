package com.scm.controller;

import com.scm.service.AuthService;
import com.scm.util.JsonUtil;
import io.javalin.http.Context;
import java.util.Map;

/**
 * AuthController - MVC Controller for authentication endpoints.
 *
 * MVC: Controller layer - handles HTTP, delegates to AuthService
 * GRASP: Controller - thin, delegates all business logic
 * SOLID: SRP - only handles auth HTTP endpoints
 */
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    /** POST /api/auth/login */
    public void login(Context ctx) {
        try {
            Map<String, Object> body = JsonUtil.fromJson(ctx.body(), Map.class);
            String email    = (String) body.get("email");
            String password = (String) body.get("password");

            if (email == null || password == null) {
                ctx.status(400).json(Map.of("error", "Email and password are required"));
                return;
            }
            Map<String, Object> session = authService.login(email, password);
            if (session != null) {
                ctx.json(session);
            } else {
                ctx.status(401).json(Map.of("error", "Invalid email or password"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    /** POST /api/auth/logout */
    public void logout(Context ctx) {
        try {
            Map<String, Object> body = JsonUtil.fromJson(ctx.body(), Map.class);
            String token = (String) body.get("token");
            if (token != null && authService.logout(token)) {
                ctx.json(Map.of("success", true));
            } else {
                ctx.status(400).json(Map.of("error", "Invalid token"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }

    /** Extract and validate session from Authorization header. */
    public Map<String, Object> getSession(Context ctx) {
        String token = ctx.header("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        return token != null ? authService.validateSession(token) : null;
    }

    /** Check if request has a valid session with one of the allowed roles. */
    public boolean requireRole(Context ctx, String... allowedRoles) {
        Map<String, Object> session = getSession(ctx);
        if (session == null) {
            ctx.status(401).json(Map.of("error", "Unauthorized - please login"));
            return false;
        }
        String userRole = (String) session.get("role");
        for (String role : allowedRoles) {
            if (role.equals(userRole)) return true;
        }
        ctx.status(403).json(Map.of("error", "Forbidden - insufficient permissions"));
        return false;
    }
}
