package com.scm.model;

import java.util.List;
import java.util.Map;

/**
 * PerformanceReport - built by ReportBuilder (Builder Pattern).
 * Contains aggregated vendor performance and risk metrics.
 */
public class PerformanceReport {
    private String generatedAt;
    private String reportType;
    private List<Map<String, Object>> vendorSummaries;
    private Map<String, Object> statistics;

    public PerformanceReport() {}

    public String getGeneratedAt()                           { return generatedAt; }
    public void setGeneratedAt(String t)                     { this.generatedAt = t; }
    public String getReportType()                            { return reportType; }
    public void setReportType(String t)                      { this.reportType = t; }
    public List<Map<String, Object>> getVendorSummaries()   { return vendorSummaries; }
    public void setVendorSummaries(List<Map<String, Object>> s) { this.vendorSummaries = s; }
    public Map<String, Object> getStatistics()               { return statistics; }
    public void setStatistics(Map<String, Object> s)         { this.statistics = s; }
}
