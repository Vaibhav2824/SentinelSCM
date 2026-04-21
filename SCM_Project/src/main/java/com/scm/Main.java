package com.scm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scm.controller.*;
import com.scm.db.DatabaseManager;
import com.scm.db.MockDataSeeder;
import com.scm.db.SchemaInitializer;
import com.scm.repository.*;
import com.scm.service.*;

import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Main - Application entry point.
 *
 * Architecture: HTML/JS Frontend ←→ Java/Javalin REST API ←→ MySQL Database
 * MVC: Wires together Controller → Service → Repository layers
 * Dependency Injection: manual, demonstrating DIP
 *
 * Design Patterns demonstrated in this wiring:
 * - Singleton: DatabaseManager.getInstance()
 * - Strategy:  WeightedRiskStrategy
 * - Observer:  AlertService + ConsoleLogObserver
 * - Factory:   AlertFactory (used inside RiskService)
 * - Builder:   ReportBuilder (used inside ReportService)
 * - MVC:       Controller → Service → Repository chain
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Smart Supply Chain - Vendor Risk Management");
        System.out.println("  OOAD Mini Project | PES University UE23CS352B");
        System.out.println("=================================================\n");

        // Phase 1: Initialize Database (Singleton pattern)
        System.out.println("[STARTUP] Connecting to MySQL...");
        DatabaseManager.getInstance();
        System.out.println("[STARTUP] Initializing schema...");
        SchemaInitializer.initialize();
        System.out.println("[STARTUP] Seeding demo data...");
        MockDataSeeder.seed();

        // Phase 2: Create Repository instances (DIP — depend on interfaces)
        IVendorRepository   vendorRepo    = new MySQLVendorRepository();
        IRiskRepository     riskRepo      = new MySQLRiskRepository();
        IAlertRepository    alertRepo     = new MySQLAlertRepository();
        IInventoryRepository inventoryRepo = new MySQLInventoryRepository();

        // Phase 3: Create Services with Dependency Injection
        AuthService authService = new AuthService();

        // Observer pattern: register observers
        AlertService alertService = new AlertService();
        alertService.addObserver(new ConsoleLogObserver());

        // Strategy pattern: select risk algorithm
        IRiskStrategy riskStrategy = new WeightedRiskStrategy();

        // Pure Fabrication: recommendation service
        RecommendationService recService = new RecommendationService(vendorRepo, alertRepo);

        // Core services
        VendorService    vendorService    = new VendorService(vendorRepo);
        RiskService      riskService      = new RiskService(riskStrategy, riskRepo, vendorRepo, alertService, alertRepo, recService);
        InventoryService inventoryService = new InventoryService(inventoryRepo);
        ReportService    reportService    = new ReportService(vendorRepo, riskRepo, alertRepo);

        // Phase 4: Create Controllers (MVC Controller layer)
        AuthController      authController      = new AuthController(authService);
        VendorController    vendorController    = new VendorController(vendorService, authController);
        RiskController      riskController      = new RiskController(riskService, riskRepo, authController);
        AlertController     alertController     = new AlertController(alertRepo, authController);
        InventoryController inventoryController = new InventoryController(inventoryService, authController);
        ReportController    reportController    = new ReportController(reportService, authController);

        // Configure Gson as JSON mapper for Javalin
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @NotNull @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
            @NotNull @Override
            public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }
            @NotNull @Override
            public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
                return gson.fromJson(new java.io.InputStreamReader(json), targetType);
            }
        };

        // Phase 5: Start Javalin HTTP Server
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(gsonMapper);
            // CORS: allow all origins for local development
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                    it.allowCredentials = true;
                });
            });
            // Serve static frontend files — absolute path resolved at runtime
            String frontendPath = System.getProperty("user.dir") + java.io.File.separator + "frontend";
            System.out.println("[STATIC] Serving frontend from: " + frontendPath);
            config.staticFiles.add(sf -> {
                sf.hostedPath = "/";
                sf.directory  = frontendPath;
                sf.location   = io.javalin.http.staticfiles.Location.EXTERNAL;
            });
        });

        // ===== AUTH ENDPOINTS =====
        app.post("/api/auth/login",  authController::login);
        app.post("/api/auth/logout", authController::logout);

        // ===== VENDOR ENDPOINTS =====
        app.get( "/api/vendor",              vendorController::getAll);
        app.get( "/api/vendor/{id}",         vendorController::getById);
        app.post("/api/vendor",              vendorController::create);
        app.put( "/api/vendor/{id}",         vendorController::update);
        app.delete("/api/vendor/{id}",       vendorController::delete);
        app.put("/api/vendor/{id}/suspend",  vendorController::suspend);
        app.put("/api/vendor/{id}/blacklist",vendorController::blacklist);
        app.put("/api/vendor/{id}/activate", vendorController::activate);

        // ===== RISK ENDPOINTS =====
        app.get( "/api/vendor/{id}/risk",          riskController::getRiskScore);
        app.post("/api/risk/calculate/{vendorId}",  riskController::calculateRisk);
        app.post("/api/vendor/{id}/evaluate",       riskController::submitEvaluation);

        // ===== ALERT ENDPOINTS =====
        app.get("/api/alert",                       alertController::getAll);
        app.get("/api/alert/{id}/recommendation",   alertController::getRecommendations);
        app.put("/api/alert/{id}/resolve",          alertController::resolve);

        // ===== INVENTORY ENDPOINTS =====
        app.get("/api/inventory",       inventoryController::getAll);
        app.put("/api/inventory/{id}",  inventoryController::updateStock);

        // ===== REPORT ENDPOINTS =====
        app.get("/api/report/performance", reportController::performanceReport);
        app.get("/api/report/strategic",   reportController::strategicReport);

        // Health check
        app.get("/api/health", ctx -> ctx.json(java.util.Map.of(
            "status",   "running",
            "database", "MySQL",
            "strategy", riskStrategy.getStrategyName()
        )));

        app.start(8080);

        System.out.println("\n[SERVER] SupplyChainOS started successfully!");
        System.out.println("[SERVER] API:      http://localhost:8080/api");
        System.out.println("[SERVER] Frontend: http://localhost:8080/index.html");
        System.out.println("[SERVER] Health:   http://localhost:8080/api/health");
        System.out.println("\nPress Ctrl+C to stop.\n");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SHUTDOWN] Stopping server...");
            app.stop();
            DatabaseManager.getInstance().closeConnection();
            System.out.println("[SHUTDOWN] Done.");
        }));
    }
}
