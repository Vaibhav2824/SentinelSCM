package com.sentinelscm.builder;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportBuilderTest {

    @Test
    void buildsImmutableReportPreservingStatOrder() {
        LocalDateTime at = LocalDateTime.of(2026, 9, 14, 10, 0);

        Report report = new ReportBuilder()
            .type("PERFORMANCE")
            .generatedAt(at)
            .stat("totalVendors", 12)
            .stat("highRiskVendors", 3)
            .vendor(Map.of("name", "QuickParts"))
            .build();

        assertThat(report.reportType()).isEqualTo("PERFORMANCE");
        assertThat(report.generatedAt()).isEqualTo(at);
        assertThat(report.statistics().keySet()).containsExactly("totalVendors", "highRiskVendors");
        assertThat(report.vendors()).hasSize(1);
        assertThatThrownBy(() -> report.statistics().put("x", 1)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> report.vendors().add(Map.of())).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void requiresType() {
        assertThatThrownBy(() -> new ReportBuilder().build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("type");
    }
}
