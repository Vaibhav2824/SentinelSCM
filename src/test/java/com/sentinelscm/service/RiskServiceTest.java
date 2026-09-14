package com.sentinelscm.service;

import com.sentinelscm.domain.*;
import com.sentinelscm.event.HighRiskAlertEvent;
import com.sentinelscm.repository.*;
import com.sentinelscm.service.risk.RiskStrategy;
import com.sentinelscm.service.risk.WeightedRiskStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock EvaluationCriteriaRepository criteria;
    @Mock RiskScoreRepository scores;
    @Mock VendorRepository vendors;
    @Mock AlertRepository alerts;
    @Mock RiskRuleRepository rules;
    @Mock ApplicationEventPublisher events;

    private RiskService service;
    private Vendor vendor;

    @BeforeEach
    void setUp() {
        service = new RiskService(List.of(new WeightedRiskStrategy(), new ConstantStrategy(0.95)), "weighted",
            criteria, scores, vendors, alerts, rules, events);
        vendor = new Vendor("FastSupply Co", "orders@fastsupply.com", 2.1, 0.5, VendorStatus.ACTIVE);
        ReflectionTestUtils.setField(vendor, "id", 2);
    }

    @Test
    void scoreBelowThresholdUpdatesVendorWithoutAlert() {
        when(vendors.findById(2)).thenReturn(Optional.of(vendor));
        when(criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(2))
            .thenReturn(Optional.of(new EvaluationCriteria(2, 0.9, 0.05, 0.95, LocalDate.now())));
        when(rules.findTopByOrderByIdDesc()).thenReturn(Optional.of(new RiskRule(0.7, 1)));

        RiskResult result = service.calculateRisk(2);

        assertThat(result.alertTriggered()).isFalse();
        assertThat(result.score()).isEqualTo(0.07);
        assertThat(vendor.getStatus()).isEqualTo(VendorStatus.ACTIVE);
        assertThat(vendor.getRiskScore()).isEqualTo(0.07);
        verify(scores).save(any(RiskScore.class));
        verifyNoInteractions(alerts, events);
    }

    @Test
    void scoreAboveThresholdRaisesAlertAndPublishesEvent() {
        when(vendors.findById(2)).thenReturn(Optional.of(vendor));
        when(criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(2))
            .thenReturn(Optional.of(new EvaluationCriteria(2, 0.45, 0.35, 0.60, LocalDate.now())));
        when(rules.findTopByOrderByIdDesc()).thenReturn(Optional.of(new RiskRule(0.3, 1)));
        when(alerts.save(any(Alert.class))).thenAnswer(inv -> {
            Alert a = inv.getArgument(0);
            ReflectionTestUtils.setField(a, "id", 77);
            return a;
        });

        RiskResult result = service.calculateRisk(2);

        assertThat(result.alertTriggered()).isTrue();
        assertThat(result.alertId()).isEqualTo(77);
        assertThat(result.score()).isEqualTo(0.44);
        assertThat(vendor.getStatus()).isEqualTo(VendorStatus.ACTIVE); // 0.44 is MEDIUM, not HIGH
        ArgumentCaptor<HighRiskAlertEvent> captor = ArgumentCaptor.forClass(HighRiskAlertEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().alert().getSeverity()).isEqualTo(Severity.HIGH);
        assertThat(captor.getValue().threshold()).isEqualTo(0.3);
    }

    @Test
    void highScoreFlipsVendorToHighRiskStatus() {
        when(vendors.findById(2)).thenReturn(Optional.of(vendor));
        when(criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(2))
            .thenReturn(Optional.of(new EvaluationCriteria(2, 0.1, 0.9, 0.1, LocalDate.now())));
        when(rules.findTopByOrderByIdDesc()).thenReturn(Optional.empty()); // falls back to default 0.7
        when(alerts.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        RiskResult result = service.calculateRisk(2);

        assertThat(result.threshold()).isEqualTo(RiskRule.DEFAULT_THRESHOLD);
        assertThat(vendor.getStatus()).isEqualTo(VendorStatus.HIGH_RISK);
    }

    @Test
    void missingCriteriaIsAnIllegalState() {
        when(vendors.findById(2)).thenReturn(Optional.of(vendor));
        when(criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calculateRisk(2))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Submit an evaluation first");
    }

    @Test
    void unknownVendorIsNotFound() {
        when(vendors.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.calculateRisk(99)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void strategyCanBeSwappedAtRuntime() {
        assertThat(service.activeStrategy()).isEqualTo("weighted");
        service.useStrategy("constant");
        assertThat(service.activeStrategy()).isEqualTo("constant");
        assertThatThrownBy(() -> service.useStrategy("nope")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void submitEvaluationPersistsCriteriaAndPreviewsScore() {
        when(vendors.existsById(2)).thenReturn(true);
        when(criteria.save(any(EvaluationCriteria.class))).thenAnswer(inv -> inv.getArgument(0));

        double preview = service.submitEvaluation(2, 0.5, 0.3, 0.65);

        assertThat(preview).isEqualTo(0.39);
        verify(criteria).save(any(EvaluationCriteria.class));
        verifyNoInteractions(scores);
    }

    /** Second strategy so the runtime-swap test has something to switch to. */
    private record ConstantStrategy(double value) implements RiskStrategy {
        @Override public double calculate(EvaluationCriteria c) { return value; }
        @Override public String name() { return "constant"; }
    }
}
