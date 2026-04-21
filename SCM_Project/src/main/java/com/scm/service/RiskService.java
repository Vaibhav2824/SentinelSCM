package com.scm.service;

import com.scm.factory.AlertFactory;
import com.scm.model.*;
import com.scm.repository.IAlertRepository;
import com.scm.repository.IRiskRepository;
import com.scm.repository.IVendorRepository;
import java.util.*;

/**
 * RiskService - Core risk calculation business logic.
 *
 * SOLID: SRP - only handles risk calculation orchestration
 * SOLID: OCP - uses IRiskStrategy; new algorithms without changes here
 * SOLID: DIP - depends on interfaces not MySQL implementations
 * GRASP: Information Expert - has criteria, rules, scores = knows how to calculate risk
 * GRASP: Low Coupling - depends on interfaces only
 * GRASP: Controller - orchestrates: calculate → alert (Factory) → notify (Observer) → recommend (Pure Fabrication)
 * Design Pattern: Strategy - pluggable risk algorithm via IRiskStrategy
 */
public class RiskService {

    private IRiskStrategy strategy;
    private final IRiskRepository riskRepo;
    private final IVendorRepository vendorRepo;
    private final AlertService alertService;
    private final IAlertRepository alertRepo;
    private final RecommendationService recService;

    public RiskService(IRiskStrategy strategy, IRiskRepository riskRepo, IVendorRepository vendorRepo,
                       AlertService alertService, IAlertRepository alertRepo, RecommendationService recService) {
        this.strategy = strategy; this.riskRepo = riskRepo; this.vendorRepo = vendorRepo;
        this.alertService = alertService; this.alertRepo = alertRepo; this.recService = recService;
    }

    /** Switch risk algorithm at runtime (Strategy pattern). */
    public void setStrategy(IRiskStrategy strategy) { this.strategy = strategy; }

    /**
     * Full risk calculation pipeline:
     * 1. Load latest evaluation criteria
     * 2. Calculate score (Strategy)
     * 3. Save risk score
     * 4. Update vendor status
     * 5. If score > threshold → Factory creates alert → Observer notifies → Pure Fabrication recommends
     */
    public Map<String, Object> calculateRisk(int vendorId) {
        Map<String, Object> result = new HashMap<>();

        EvaluationCriteria criteria = riskRepo.findLatestCriteria(vendorId);
        if (criteria == null)
            throw new IllegalStateException("No evaluation criteria found for vendor " + vendorId + ". Submit evaluation first.");

        // Step 2: Strategy pattern
        double score = strategy.calculate(criteria);
        double rounded = Math.round(score * 1000.0) / 1000.0;
        result.put("score", rounded);
        result.put("strategy", strategy.getStrategyName());

        // Step 3: Save score
        RiskScore rs = new RiskScore();
        rs.setVendorId(vendorId); rs.setScore(score);
        riskRepo.saveRiskScore(rs);

        // Step 4: Update vendor
        Vendor vendor = vendorRepo.findById(vendorId);
        vendor.updatePerformance(vendor.getRating(), score);
        vendorRepo.updateRiskScore(vendorId, score);
        vendorRepo.updateStatus(vendorId, vendor.getStatus());

        double threshold = riskRepo.getThreshold();
        result.put("threshold", threshold);
        result.put("alertTriggered", false);

        // Step 5: Threshold check → Factory → Observer → Pure Fabrication
        if (score > threshold) {
            Alert alert = AlertFactory.createAlert(vendor, score);
            int alertId = alertRepo.saveAlert(alert);
            alert.setAlertId(alertId);

            alertService.fireAlert(alert);            // Observer pattern
            recService.suggestAlternatives(vendor, alertId); // Pure Fabrication

            result.put("alertTriggered", true);
            result.put("alertId", alertId);
        }

        return result;
    }

    /** Submit new evaluation criteria and return preview score. */
    public double submitEvaluation(int vendorId, double delivery, double defect, double compliance) {
        EvaluationCriteria ec = new EvaluationCriteria();
        ec.setVendorId(vendorId); ec.setDeliveryTimeliness(delivery);
        ec.setDefectRate(defect); ec.setComplianceScore(compliance);
        riskRepo.saveCriteria(ec);
        return strategy.calculate(ec);
    }

    /** Get risk history for a vendor. */
    public List<RiskScore> getRiskHistory(int vendorId) {
        return riskRepo.findRiskHistory(vendorId);
    }

    public Map<String, Object> getConfig() {
        return Map.of("strategy", strategy.getStrategyName(), "threshold", riskRepo.getThreshold());
    }
}
