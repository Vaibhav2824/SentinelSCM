package com.scm.builder;

import java.util.*;

/**
 * ReportBuilder - Builder Pattern for constructing report maps.
 *
 * Design Pattern: Builder
 * SOLID: SRP - only builds reports; nothing else
 * SOLID: OCP - new report fields added by chaining methods
 * GRASP: Low Coupling - callers (ReportService) use fluent API without knowing internal structure
 */
public class ReportBuilder {

    private String type;
    private String generatedAt;
    private List<Map<String, Object>> vendorSummaries;
    private final Map<String, Object> statistics = new LinkedHashMap<>();

    /** Set report type (PERFORMANCE or STRATEGIC). */
    public ReportBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /** Set generation timestamp. */
    public ReportBuilder setGeneratedAt(String ts) {
        this.generatedAt = ts;
        return this;
    }

    /** Set list of vendor summaries. */
    public ReportBuilder setVendorSummaries(List<Map<String, Object>> summaries) {
        this.vendorSummaries = summaries;
        return this;
    }

    /** Add a statistic key-value pair. */
    public ReportBuilder addStat(String key, Object value) {
        this.statistics.put(key, value);
        return this;
    }

    /** Build and return the final report as a Map. */
    public Map<String, Object> build() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType",      type);
        report.put("generatedAt",     generatedAt);
        report.put("statistics",      statistics);
        if (vendorSummaries != null) {
            report.put("vendors", vendorSummaries);
        }
        return report;
    }
}
