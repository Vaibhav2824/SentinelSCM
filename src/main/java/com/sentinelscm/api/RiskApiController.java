package com.sentinelscm.api;

import com.sentinelscm.api.dto.EvaluationRequest;
import com.sentinelscm.api.dto.ThresholdRequest;
import com.sentinelscm.domain.RiskScore;
import com.sentinelscm.security.Access;
import com.sentinelscm.security.SecurityUser;
import com.sentinelscm.service.RiskResult;
import com.sentinelscm.service.RiskService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RiskApiController {

    private final RiskService risk;

    public RiskApiController(RiskService risk) {
        this.risk = risk;
    }

    @GetMapping("/vendor/{id}/risk")
    @PreAuthorize(Access.ADMIN_PM_RA_VENDOR)
    public List<RiskScore> history(@PathVariable Integer id) {
        return risk.history(id);
    }

    @PostMapping("/vendor/{id}/evaluate")
    @PreAuthorize(Access.ADMIN_PM_RA)
    public Map<String, Object> evaluate(@PathVariable Integer id, @Valid @RequestBody EvaluationRequest req) {
        double preview = risk.submitEvaluation(id, req.deliveryTimeliness(), req.defectRate(), req.complianceScore());
        return Map.of("vendorId", id, "previewScore", preview, "strategy", risk.activeStrategy());
    }

    @PostMapping("/risk/calculate/{vendorId}")
    @PreAuthorize(Access.ADMIN_RA)
    public RiskResult calculate(@PathVariable Integer vendorId) {
        return risk.calculateRisk(vendorId);
    }

    @GetMapping("/risk/config")
    @PreAuthorize(Access.ANY_USER)
    public Map<String, Object> config() {
        return risk.config();
    }

    @PutMapping("/risk/threshold")
    @PreAuthorize(Access.ADMIN)
    public Map<String, Object> updateThreshold(@Valid @RequestBody ThresholdRequest req,
                                               @AuthenticationPrincipal SecurityUser user) {
        risk.updateThreshold(req.threshold(), user.getId());
        return risk.config();
    }
}
