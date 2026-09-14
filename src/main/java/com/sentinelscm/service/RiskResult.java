package com.sentinelscm.service;

/** Outcome of a risk calculation run. */
public record RiskResult(
    Integer vendorId,
    double score,
    String strategy,
    double threshold,
    boolean alertTriggered,
    Integer alertId
) { }
