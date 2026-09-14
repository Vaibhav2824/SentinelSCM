package com.sentinelscm.service;

import com.sentinelscm.builder.Report;
import com.sentinelscm.builder.ReportBuilder;
import com.sentinelscm.domain.*;
import com.sentinelscm.repository.AlertRepository;
import com.sentinelscm.repository.EvaluationCriteriaRepository;
import com.sentinelscm.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builds performance and strategic reports through ReportBuilder (Builder pattern). */
@Service
@Transactional(readOnly = true)
public class ReportService {

    static final int TOP_RISK_LIMIT = 5;

    private final VendorRepository vendors;
    private final EvaluationCriteriaRepository criteria;
    private final AlertRepository alerts;

    public ReportService(VendorRepository vendors, EvaluationCriteriaRepository criteria, AlertRepository alerts) {
        this.vendors = vendors;
        this.criteria = criteria;
        this.alerts = alerts;
    }

    public Report performance() {
        List<Vendor> all = vendors.findByStatusNotOrderByIdAsc(VendorStatus.INACTIVE);
        ReportBuilder b = new ReportBuilder().type("PERFORMANCE");
        all.forEach(v -> b.vendor(summarise(v)));
        return b.stat("totalVendors", all.size())
            .stat("activeVendors", all.stream().filter(v -> v.getStatus() == VendorStatus.ACTIVE).count())
            .stat("highRiskVendors", all.stream().filter(v -> v.riskLevel() == RiskLevel.HIGH).count())
            .stat("avgRiskScore", round3(all.stream().mapToDouble(Vendor::getRiskScore).average().orElse(0.0)))
            .stat("unresolvedAlerts", alerts.countByResolvedFalse())
            .build();
    }

    public Report strategic() {
        List<Vendor> all = vendors.findByStatusNotOrderByIdAsc(VendorStatus.INACTIVE);
        List<Map<String, Object>> topRisk = all.stream()
            .sorted(Comparator.comparingDouble(Vendor::getRiskScore).reversed())
            .limit(TOP_RISK_LIMIT)
            .map(v -> Map.<String, Object>of("name", v.getName(), "riskScore", v.getRiskScore(), "status", v.getStatus().name()))
            .toList();
        return new ReportBuilder().type("STRATEGIC")
            .stat("totalVendors", all.size())
            .stat("lowRiskCount", all.stream().filter(v -> v.riskLevel() == RiskLevel.LOW).count())
            .stat("mediumRiskCount", all.stream().filter(v -> v.riskLevel() == RiskLevel.MEDIUM).count())
            .stat("highRiskCount", all.stream().filter(v -> v.riskLevel() == RiskLevel.HIGH).count())
            .stat("totalAlerts", alerts.count())
            .stat("unresolvedAlerts", alerts.countByResolvedFalse())
            .stat("top5HighRisk", topRisk)
            .build();
    }

    private Map<String, Object> summarise(Vendor v) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("vendorId", v.getId());
        row.put("name", v.getName());
        row.put("status", v.getStatus().name());
        row.put("rating", v.getRating());
        row.put("riskScore", round3(v.getRiskScore()));
        row.put("riskLevel", v.riskLevel().name());
        criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(v.getId()).ifPresent(ec -> {
            row.put("deliveryTimeliness", ec.getDeliveryTimeliness());
            row.put("defectRate", ec.getDefectRate());
            row.put("complianceScore", ec.getComplianceScore());
        });
        return row;
    }

    private static double round3(double d) { return Math.round(d * 1000.0) / 1000.0; }
}
