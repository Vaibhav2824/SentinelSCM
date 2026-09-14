package com.sentinelscm.service;

import com.sentinelscm.builder.Report;
import com.sentinelscm.builder.ReportBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportCsvWriterTest {

    @Test
    void writesStatsBlockThenVendorTableWithUnionOfColumns() {
        Map<String, Object> full = new LinkedHashMap<>();
        full.put("name", "Quick, Parts");
        full.put("riskScore", 0.12);
        Map<String, Object> partial = new LinkedHashMap<>();
        partial.put("name", "Say \"hi\"");

        Report report = new ReportBuilder().type("PERFORMANCE")
            .generatedAt(LocalDateTime.of(2026, 9, 14, 9, 30))
            .stat("totalVendors", 2)
            .vendor(full).vendor(partial)
            .build();

        String csv = ReportCsvWriter.toCsv(report);

        assertThat(csv).startsWith("report_type,PERFORMANCE\r\ngenerated_at,2026-09-14T09:30\r\ntotalVendors,2\r\n\r\n");
        assertThat(csv).contains("name,riskScore\r\n");
        assertThat(csv).contains("\"Quick, Parts\",0.12\r\n");
        assertThat(csv).contains("\"Say \"\"hi\"\"\",\r\n");
    }

    @Test
    void omitsTableWhenNoVendors() {
        String csv = ReportCsvWriter.toCsv(new ReportBuilder().type("STRATEGIC").stat("x", 1).build());
        assertThat(csv).endsWith("x,1\r\n");
        assertThat(csv).doesNotContain("\r\n\r\n");
    }
}
