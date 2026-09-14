package com.sentinelscm.builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder pattern: fluent assembly of a Report. Statistics keep insertion order so
 * CSV columns and JSON keys come out in the order they were added.
 */
public class ReportBuilder {

    private String type;
    private LocalDateTime generatedAt = LocalDateTime.now();
    private final Map<String, Object> statistics = new LinkedHashMap<>();
    private final List<Map<String, Object>> vendors = new ArrayList<>();

    public ReportBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ReportBuilder generatedAt(LocalDateTime at) {
        this.generatedAt = at;
        return this;
    }

    public ReportBuilder stat(String key, Object value) {
        statistics.put(key, value);
        return this;
    }

    public ReportBuilder vendor(Map<String, Object> summary) {
        vendors.add(Collections.unmodifiableMap(new LinkedHashMap<>(summary)));
        return this;
    }

    public Report build() {
        if (type == null || type.isBlank()) throw new IllegalStateException("Report type is required");
        return new Report(type, generatedAt,
            Collections.unmodifiableMap(new LinkedHashMap<>(statistics)),
            List.copyOf(vendors));
    }
}
