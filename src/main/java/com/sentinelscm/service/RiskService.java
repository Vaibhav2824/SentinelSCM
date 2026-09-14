package com.sentinelscm.service;

import com.sentinelscm.domain.*;
import com.sentinelscm.event.HighRiskAlertEvent;
import com.sentinelscm.factory.AlertFactory;
import com.sentinelscm.repository.*;
import com.sentinelscm.service.risk.RiskStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Risk pipeline: latest criteria -> Strategy -> persist score -> vendor status ->
 * threshold check -> Factory builds alert -> Observer event fans out (log + recommendations).
 *
 * SOLID OCP: new algorithms are new RiskStrategy beans; nothing here changes.
 */
@Service
@Transactional
public class RiskService {

    private final Map<String, RiskStrategy> strategies;
    private final EvaluationCriteriaRepository criteria;
    private final RiskScoreRepository scores;
    private final VendorRepository vendors;
    private final AlertRepository alerts;
    private final RiskRuleRepository rules;
    private final ApplicationEventPublisher events;

    private volatile RiskStrategy strategy;

    public RiskService(List<RiskStrategy> availableStrategies,
                       @Value("${sentinel.risk.strategy:weighted}") String activeStrategy,
                       EvaluationCriteriaRepository criteria,
                       RiskScoreRepository scores,
                       VendorRepository vendors,
                       AlertRepository alerts,
                       RiskRuleRepository rules,
                       ApplicationEventPublisher events) {
        this.strategies = availableStrategies.stream()
            .collect(Collectors.toUnmodifiableMap(RiskStrategy::name, Function.identity()));
        this.criteria = criteria;
        this.scores = scores;
        this.vendors = vendors;
        this.alerts = alerts;
        this.rules = rules;
        this.events = events;
        this.strategy = resolve(activeStrategy);
    }

    /** Strategy pattern: swap the algorithm at runtime. */
    public void useStrategy(String name) { this.strategy = resolve(name); }

    public String activeStrategy() { return strategy.name(); }

    public RiskResult calculateRisk(Integer vendorId) {
        Vendor vendor = vendors.findById(vendorId).orElseThrow(() -> new NotFoundException("Vendor", vendorId));
        EvaluationCriteria latest = criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(vendorId)
            .orElseThrow(() -> new IllegalStateException(
                "No evaluation criteria for vendor " + vendorId + ". Submit an evaluation first."));

        double score = strategy.calculate(latest);
        scores.save(new RiskScore(vendorId, score));
        vendor.updatePerformance(vendor.getRating(), score);

        double threshold = threshold();
        if (score <= threshold) {
            return new RiskResult(vendorId, score, strategy.name(), threshold, false, null);
        }
        Alert alert = alerts.save(AlertFactory.highRiskAlert(vendor, score, threshold));
        events.publishEvent(new HighRiskAlertEvent(alert, vendor, score, threshold));
        return new RiskResult(vendorId, score, strategy.name(), threshold, true, alert.getId());
    }

    /** Store a new evaluation and return the score it would produce (preview, not persisted as a score). */
    public double submitEvaluation(Integer vendorId, double timeliness, double defectRate, double compliance) {
        if (!vendors.existsById(vendorId)) throw new NotFoundException("Vendor", vendorId);
        EvaluationCriteria ec = criteria.save(
            new EvaluationCriteria(vendorId, timeliness, defectRate, compliance, LocalDate.now()));
        return strategy.calculate(ec);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<EvaluationCriteria> latestCriteria(Integer vendorId) {
        return criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(vendorId);
    }

    @Transactional(readOnly = true)
    public List<RiskScore> history(Integer vendorId) {
        return scores.findByVendorIdOrderByCalculatedDateDesc(vendorId);
    }

    @Transactional(readOnly = true)
    public double threshold() {
        return rules.findTopByOrderByIdDesc().map(RiskRule::getThreshold).orElse(RiskRule.DEFAULT_THRESHOLD);
    }

    /** GRASP Protected Variations: the threshold is data, not code. Appends a new rule row. */
    public RiskRule updateThreshold(double threshold, Integer byUserId) {
        return rules.save(new RiskRule(threshold, byUserId));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> config() {
        return Map.of("strategy", strategy.name(), "threshold", threshold(),
            "availableStrategies", strategies.keySet().stream().sorted().toList());
    }

    private RiskStrategy resolve(String name) {
        RiskStrategy s = strategies.get(name);
        if (s == null) throw new IllegalArgumentException("Unknown risk strategy: " + name + ". Known: " + strategies.keySet());
        return s;
    }
}
