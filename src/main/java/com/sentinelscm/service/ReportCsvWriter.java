package com.sentinelscm.service;

import com.sentinelscm.builder.Report;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Renders a Report as RFC 4180 CSV: a statistics block, then one row per vendor summary. */
public final class ReportCsvWriter {

    private ReportCsvWriter() { }

    public static String toCsv(Report report) {
        StringBuilder sb = new StringBuilder();
        sb.append("report_type,").append(escape(report.reportType())).append("\r\n");
        sb.append("generated_at,").append(escape(String.valueOf(report.generatedAt()))).append("\r\n");
        report.statistics().forEach((k, v) -> sb.append(escape(k)).append(',').append(escape(String.valueOf(v))).append("\r\n"));

        if (report.vendors().isEmpty()) return sb.toString();

        Set<String> columns = new LinkedHashSet<>();
        report.vendors().forEach(row -> columns.addAll(row.keySet()));
        sb.append("\r\n").append(columns.stream().map(ReportCsvWriter::escape).collect(Collectors.joining(","))).append("\r\n");
        for (Map<String, Object> row : report.vendors()) {
            sb.append(columns.stream()
                .map(c -> escape(row.get(c) == null ? "" : String.valueOf(row.get(c))))
                .collect(Collectors.joining(","))).append("\r\n");
        }
        return sb.toString();
    }

    private static String escape(String value) {
        if (value == null) return "";
        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
