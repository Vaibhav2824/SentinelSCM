package com.scm.service;

import com.scm.builder.ReportBuilder;
import com.scm.model.*;
import com.scm.repository.IAlertRepository;
import com.scm.repository.IRiskRepository;
import com.scm.repository.IVendorRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportService - generates performance and strategic reports.
 *
 * GRASP: Information Expert - has access to all data sources
 * SOLID: SRP - only handles report generation
 * Design Pattern: Builder - uses ReportBuilder to construct reports
 */
public class ReportService {

    private final IVendorRepository vendorRepo;
    private final IRiskRepository riskRepo;
    private final IAlertRepository alertRepo;

    public ReportService(IVendorRepository vendorRepo, IRiskRepository riskRepo, IAlertRepository alertRepo) {
        this.vendorRepo = vendorRepo; this.riskRepo = riskRepo; this.alertRepo = alertRepo;
    }

    /** Performance report: per-vendor metrics and KPIs. */
    public Map<String, Object> generatePerformanceReport() {
        List<Vendor> vendors = vendorRepo.findAll();
        List<Alert> alerts = alertRepo.findAll();

        List<Map<String, Object>> summaries = new ArrayList<>();
        for (Vendor v : vendors) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("vendorId",   v.getVendorId());
            entry.put("name",       v.getName());
            entry.put("status",     v.getStatus());
            entry.put("rating",     v.getRating());
            entry.put("riskScore",  Math.round(v.getRiskScore() * 1000.0) / 1000.0);
            entry.put("riskLevel",  v.getRiskLevel());

            EvaluationCriteria ec = riskRepo.findLatestCriteria(v.getVendorId());
            if (ec != null) {
                entry.put("deliveryTimeliness", ec.getDeliveryTimeliness());
                entry.put("defectRate",         ec.getDefectRate());
                entry.put("complianceScore",    ec.getComplianceScore());
            }
            summaries.add(entry);
        }

        long highRisk = vendors.stream().filter(v -> v.getRiskScore() > 0.7).count();
        long activeCount = vendors.stream().filter(v -> "ACTIVE".equals(v.getStatus())).count();
        long unresolvedAlerts = alerts.stream().filter(a -> !a.isResolved()).count();
        double avgRisk = vendors.stream().mapToDouble(Vendor::getRiskScore).average().orElse(0.0);

        // Builder Pattern: construct report
        return new ReportBuilder()
            .setType("PERFORMANCE")
            .setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setVendorSummaries(summaries)
            .addStat("totalVendors",     vendors.size())
            .addStat("activeVendors",    activeCount)
            .addStat("highRiskVendors",  highRisk)
            .addStat("avgRiskScore",     Math.round(avgRisk * 1000.0) / 1000.0)
            .addStat("unresolvedAlerts", unresolvedAlerts)
            .build();
    }

    /** Strategic report: system-wide risk posture and recommendations. */
    public Map<String, Object> generateStrategicReport() {
        List<Vendor> vendors = vendorRepo.findAll();
        List<Alert> alerts = alertRepo.findAll();

        long low    = vendors.stream().filter(v -> v.getRiskScore() <= 0.4).count();
        long medium = vendors.stream().filter(v -> v.getRiskScore() > 0.4 && v.getRiskScore() <= 0.7).count();
        long high   = vendors.stream().filter(v -> v.getRiskScore() > 0.7).count();

        List<Map<String, Object>> topRisk = new ArrayList<>();
        vendors.stream().sorted(Comparator.comparingDouble(Vendor::getRiskScore).reversed())
            .limit(5)
            .forEach(v -> topRisk.add(Map.of(
                "name", v.getName(), "riskScore", v.getRiskScore(), "status", v.getStatus()
            )));

        return new ReportBuilder()
            .setType("STRATEGIC")
            .setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addStat("totalVendors",    vendors.size())
            .addStat("lowRiskCount",    low)
            .addStat("mediumRiskCount", medium)
            .addStat("highRiskCount",   high)
            .addStat("totalAlerts",     alerts.size())
            .addStat("unresolvedAlerts", alerts.stream().filter(a -> !a.isResolved()).count())
            .addStat("top5HighRisk",    topRisk)
            .build();
    }
}
