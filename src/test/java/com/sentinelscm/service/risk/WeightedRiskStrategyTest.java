package com.sentinelscm.service.risk;

import com.sentinelscm.domain.EvaluationCriteria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class WeightedRiskStrategyTest {

    private final WeightedRiskStrategy strategy = new WeightedRiskStrategy();

    private static EvaluationCriteria criteria(double timeliness, double defects, double compliance) {
        return new EvaluationCriteria(1, timeliness, defects, compliance, LocalDate.now());
    }

    @Test
    void perfectVendorScoresZero() {
        assertThat(strategy.calculate(criteria(1.0, 0.0, 1.0))).isEqualTo(0.0);
    }

    @Test
    void worstVendorScoresOne() {
        assertThat(strategy.calculate(criteria(0.0, 1.0, 0.0))).isEqualTo(1.0);
    }

    @Test
    void appliesDocumentedWeights() {
        // (1-0.5)*0.4 + 0.3*0.4 + (1-0.65)*0.2 = 0.2 + 0.12 + 0.07 = 0.39
        assertThat(strategy.calculate(criteria(0.5, 0.3, 0.65))).isCloseTo(0.39, within(1e-9));
    }

    @Test
    void roundsToThreeDecimals() {
        // (1-0.333)*0.4 + 0.111*0.4 + (1-0.777)*0.2 = 0.2668 + 0.0444 + 0.0446 = 0.3558 -> 0.356
        assertThat(strategy.calculate(criteria(0.333, 0.111, 0.777))).isEqualTo(0.356);
    }

    @Test
    void clampsOutOfRangeInputs() {
        assertThat(strategy.calculate(criteria(1.7, -0.4, 2.0))).isEqualTo(0.0);
        assertThat(strategy.calculate(criteria(-3.0, 9.0, -1.0))).isEqualTo(1.0);
    }

    @Test
    void exposesStableName() {
        assertThat(strategy.name()).isEqualTo("weighted");
    }
}
