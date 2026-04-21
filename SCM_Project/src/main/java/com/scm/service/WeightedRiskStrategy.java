package com.scm.service;

import com.scm.model.EvaluationCriteria;

/**
 * WeightedRiskStrategy - Concrete implementation of IRiskStrategy.
 *
 * Formula (from Implementation Plan):
 *   risk = (1 - delivery_timeliness) * 0.4
 *        + defect_rate               * 0.4
 *        + (1 - compliance_score)    * 0.2
 *
 * Design Pattern: Strategy (ConcreteStrategy role)
 * SOLID: OCP - adding new strategies doesn't modify existing code
 * GRASP: Information Expert - knows the formula and weights
 */
public class WeightedRiskStrategy implements IRiskStrategy {

    // Weights (must sum to 1.0)
    private static final double DELIVERY_WEIGHT   = 0.4;
    private static final double DEFECT_WEIGHT     = 0.4;
    private static final double COMPLIANCE_WEIGHT = 0.2;

    @Override
    public double calculate(EvaluationCriteria criteria) {
        double deliveryRisk   = (1.0 - criteria.getDeliveryTimeliness()) * DELIVERY_WEIGHT;
        double defectRisk     = criteria.getDefectRate()                  * DEFECT_WEIGHT;
        double complianceRisk = (1.0 - criteria.getComplianceScore())    * COMPLIANCE_WEIGHT;

        double score = deliveryRisk + defectRisk + complianceRisk;

        // Clamp to [0.0, 1.0]
        return Math.min(1.0, Math.max(0.0, score));
    }

    @Override
    public String getStrategyName() {
        return "Weighted Risk Strategy (D:0.4 + DR:0.4 + C:0.2)";
    }
}
