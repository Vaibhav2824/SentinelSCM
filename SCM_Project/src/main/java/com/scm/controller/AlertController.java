package com.scm.controller;

import com.scm.repository.IAlertRepository;
import io.javalin.http.Context;
import java.util.Map;

/** AlertController - MVC Controller for alert and recommendation endpoints. */
public class AlertController {

    private final IAlertRepository alertRepo;
    private final AuthController authController;

    public AlertController(IAlertRepository alertRepo, AuthController authController) {
        this.alertRepo = alertRepo; this.authController = authController;
    }

    /** GET /api/alert */
    public void getAll(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER", "RISK_ANALYST")) return;
        ctx.json(alertRepo.findAll());
    }

    /** GET /api/alert/{id}/recommendation */
    public void getRecommendations(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER", "RISK_ANALYST")) return;
        int alertId = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(alertRepo.findRecommendationsByAlertId(alertId));
    }

    /** PUT /api/alert/{id}/resolve */
    public void resolve(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER", "RISK_ANALYST")) return;
        int alertId = Integer.parseInt(ctx.pathParam("id"));
        alertRepo.resolveAlert(alertId);
        ctx.json(Map.of("success", true, "message", "Alert #" + alertId + " resolved"));
    }
}
