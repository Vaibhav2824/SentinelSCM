package com.sentinelscm.domain;

/** Risk bands shared by services, reports and the UI. Single source of truth for thresholds. */
public enum RiskLevel {
    LOW, MEDIUM, HIGH;

    public static final double MEDIUM_FLOOR = 0.4;
    public static final double HIGH_FLOOR = 0.7;

    public static RiskLevel of(double score) {
        if (score > HIGH_FLOOR) return HIGH;
        if (score > MEDIUM_FLOOR) return MEDIUM;
        return LOW;
    }
}
