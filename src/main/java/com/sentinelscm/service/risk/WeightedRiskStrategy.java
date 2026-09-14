package com.sentinelscm.service.risk;

import com.sentinelscm.domain.EvaluationCriteria;
import org.springframework.stereotype.Component;

/**
 * Default algorithm: risk = (1 - timeliness) * 0.4 + defectRate * 0.4 + (1 - compliance) * 0.2.
 * Inputs are clamped to [0,1] so a bad evaluation can never push the score out of range.
 */
@Component
public class WeightedRiskStrategy implements RiskStrategy {

    public static final String NAME = "weighted";

    static final double TIMELINESS_WEIGHT = 0.4;
    static final double DEFECT_WEIGHT = 0.4;
    static final double COMPLIANCE_WEIGHT = 0.2;

    @Override
    public double calculate(EvaluationCriteria c) {
        double lateness = 1.0 - clamp(c.getDeliveryTimeliness());
        double defects = clamp(c.getDefectRate());
        double nonCompliance = 1.0 - clamp(c.getComplianceScore());
        double score = lateness * TIMELINESS_WEIGHT + defects * DEFECT_WEIGHT + nonCompliance * COMPLIANCE_WEIGHT;
        return Math.round(score * 1000.0) / 1000.0;
    }

    @Override
    public String name() { return NAME; }

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
