package com.scm.controller;

import com.scm.model.RiskScore;
import com.scm.repository.IRiskRepository;
import com.scm.service.RiskService;
import com.scm.util.JsonUtil;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;

/** RiskController - MVC Controller for risk calculation endpoints. */
public class RiskController {

    private final RiskService riskService;
    private final IRiskRepository riskRepo;
    private final AuthController authController;

    public RiskController(RiskService riskService, IRiskRepository riskRepo, AuthController authController) {
        this.riskService = riskService; this.riskRepo = riskRepo; this.authController = authController;
    }

    /** POST /api/risk/calculate/{vendorId} */
    public void calculateRisk(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "RISK_ANALYST")) return;
        try {
            int vendorId = Integer.parseInt(ctx.pathParam("vendorId"));
            Map<String, Object> result = riskService.calculateRisk(vendorId);
            ctx.json(result);
        } catch (IllegalStateException e) {
            ctx.status(422).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Risk calculation failed: " + e.getMessage()));
        }
    }

    /** POST /api/vendor/{id}/evaluate */
    public void submitEvaluation(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "RISK_ANALYST", "PROCUREMENT_MANAGER")) return;
        try {
            int vendorId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = JsonUtil.fromJson(ctx.body(), Map.class);

            double delivery   = toDouble(body.get("deliveryTimeliness"));
            double defect     = toDouble(body.get("defectRate"));
            double compliance = toDouble(body.get("complianceScore"));

            double score = riskService.submitEvaluation(vendorId, delivery, defect, compliance);
            ctx.json(Map.of("previewScore", Math.round(score * 1000.0) / 1000.0,
                "message", "Evaluation saved. Use /api/risk/calculate/" + vendorId + " to trigger full recalculation."));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Evaluation failed: " + e.getMessage()));
        }
    }

    /** GET /api/vendor/{id}/risk - get risk history for vendor */
    public void getRiskScore(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "RISK_ANALYST", "PROCUREMENT_MANAGER", "VENDOR")) return;
        int vendorId = Integer.parseInt(ctx.pathParam("id"));
        List<RiskScore> history = riskService.getRiskHistory(vendorId);
        RiskScore latest = riskRepo.findLatestRiskScore(vendorId);
        ctx.json(Map.of("history", history, "latest", latest != null ? latest : Map.of()));
    }

    private double toDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }
}
