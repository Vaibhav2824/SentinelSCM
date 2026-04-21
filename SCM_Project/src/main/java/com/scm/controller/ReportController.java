package com.scm.controller;

import com.scm.service.ReportService;
import io.javalin.http.Context;

/** ReportController - MVC Controller for report endpoints. */
public class ReportController {

    private final ReportService reportService;
    private final AuthController authController;

    public ReportController(ReportService reportService, AuthController authController) {
        this.reportService = reportService; this.authController = authController;
    }

    /** GET /api/report/performance */
    public void performanceReport(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER", "RISK_ANALYST")) return;
        ctx.json(reportService.generatePerformanceReport());
    }

    /** GET /api/report/strategic */
    public void strategicReport(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN")) return;
        ctx.json(reportService.generateStrategicReport());
    }
}
