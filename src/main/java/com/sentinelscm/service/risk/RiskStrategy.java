package com.sentinelscm.service.risk;

import com.sentinelscm.domain.EvaluationCriteria;

/**
 * Strategy pattern: pluggable risk-scoring algorithm.
 * Implementations are Spring beans; the active one is selected by the
 * {@code sentinel.risk.strategy} property and can be swapped at runtime via RiskService.
 */
public interface RiskStrategy {

    /** Returns a score in [0,1] where higher means riskier. */
    double calculate(EvaluationCriteria criteria);

    /** Stable identifier used in configuration and API responses. */
    String name();
}
