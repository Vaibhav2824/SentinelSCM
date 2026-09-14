package com.sentinelscm.builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Immutable report produced by ReportBuilder. Serialises directly to JSON. */
public record Report(
    String reportType,
    LocalDateTime generatedAt,
    Map<String, Object> statistics,
    List<Map<String, Object>> vendors
) { }
