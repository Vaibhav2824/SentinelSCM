package com.scm.controller;

import com.scm.model.Vendor;
import com.scm.service.VendorService;
import com.scm.util.JsonUtil;
import io.javalin.http.Context;
import java.util.Map;

/** VendorController - MVC Controller for vendor management endpoints. */
public class VendorController {

    private final VendorService vendorService;
    private final AuthController authController;

    public VendorController(VendorService vendorService, AuthController authController) {
        this.vendorService = vendorService;
        this.authController = authController;
    }

    /** GET /api/vendor */
    public void getAll(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER", "RISK_ANALYST", "WAREHOUSE_MANAGER", "VENDOR")) return;
        ctx.json(vendorService.getAllVendors());
    }

    /** GET /api/vendor/{id} */
    public void getById(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER", "RISK_ANALYST", "VENDOR")) return;
        int id = Integer.parseInt(ctx.pathParam("id"));
        Vendor vendor = vendorService.getVendorById(id);
        if (vendor != null) ctx.json(vendor);
        else ctx.status(404).json(Map.of("error", "Vendor not found"));
    }

    /** POST /api/vendor */
    public void create(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER")) return;
        try {
            Vendor vendor = JsonUtil.fromJson(ctx.body(), Vendor.class);
            int id = vendorService.createVendor(vendor);
            ctx.status(201).json(Map.of("vendorId", id, "message", "Vendor created successfully"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/vendor/{id} */
    public void update(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER")) return;
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Vendor vendor = JsonUtil.fromJson(ctx.body(), Vendor.class);
            vendor.setVendorId(id);
            vendorService.updateVendor(vendor);
            ctx.json(Map.of("success", true, "message", "Vendor updated"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/vendor/{id} */
    public void delete(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN")) return;
        int id = Integer.parseInt(ctx.pathParam("id"));
        vendorService.deleteVendor(id);
        ctx.json(Map.of("success", true, "message", "Vendor deactivated"));
    }

    /** PUT /api/vendor/{id}/suspend */
    public void suspend(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER")) return;
        vendorService.suspendVendor(Integer.parseInt(ctx.pathParam("id")));
        ctx.json(Map.of("success", true));
    }

    /** PUT /api/vendor/{id}/blacklist */
    public void blacklist(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN")) return;
        vendorService.blacklistVendor(Integer.parseInt(ctx.pathParam("id")));
        ctx.json(Map.of("success", true));
    }

    /** PUT /api/vendor/{id}/activate */
    public void activate(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "PROCUREMENT_MANAGER")) return;
        vendorService.activateVendor(Integer.parseInt(ctx.pathParam("id")));
        ctx.json(Map.of("success", true));
    }
}
