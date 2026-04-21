package com.scm.service;

import com.scm.model.EvaluationCriteria;

/**
 * IRiskStrategy - Strategy Pattern Interface.
 *
 * SOLID: OCP - new risk algorithms can be added by implementing this interface
 * SOLID: ISP - focused single method interface for risk calculation
 * GRASP: Protected Variations - calling code doesn't know which algorithm runs
 */
public interface IRiskStrategy {

    /**
     * Calculate risk score from evaluation criteria.
     * @return double in range [0.0, 1.0] where 1.0 is highest risk
     */
    double calculate(EvaluationCriteria criteria);

    /** Human-readable name of this strategy (for logging/API responses). */
    String getStrategyName();
}
