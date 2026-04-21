package com.scm.controller;

import com.scm.service.InventoryService;
import com.scm.util.JsonUtil;
import io.javalin.http.Context;
import java.util.Map;

/** InventoryController - MVC Controller for inventory endpoints. */
public class InventoryController {

    private final InventoryService inventoryService;
    private final AuthController authController;

    public InventoryController(InventoryService inventoryService, AuthController authController) {
        this.inventoryService = inventoryService; this.authController = authController;
    }

    /** GET /api/inventory */
    public void getAll(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "WAREHOUSE_MANAGER", "PROCUREMENT_MANAGER")) return;
        ctx.json(inventoryService.getAllItems());
    }

    /** PUT /api/inventory/{id} */
    public void updateStock(Context ctx) {
        if (!authController.requireRole(ctx, "ADMIN", "WAREHOUSE_MANAGER")) return;
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = JsonUtil.fromJson(ctx.body(), Map.class);
            int quantity = ((Number) body.get("quantity")).intValue();
            inventoryService.updateStock(id, quantity);
            ctx.json(Map.of("success", true, "message", "Stock updated"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }
}
