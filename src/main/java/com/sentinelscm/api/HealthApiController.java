package com.sentinelscm.api;

import com.sentinelscm.service.RiskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Lightweight, unauthenticated liveness endpoint kept from v1 for compatibility. */
@RestController
public class HealthApiController {

    private final RiskService risk;

    public HealthApiController(RiskService risk) {
        this.risk = risk;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of("status", "running", "database", "MySQL", "strategy", risk.activeStrategy());
    }
}
