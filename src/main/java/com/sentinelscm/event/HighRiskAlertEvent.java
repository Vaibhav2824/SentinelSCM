package com.sentinelscm.event;

import com.sentinelscm.domain.Alert;
import com.sentinelscm.domain.Vendor;

/**
 * Observer pattern (subject side): published by RiskService when an alert is raised.
 * Any number of {@code @EventListener} beans react without RiskService knowing them.
 */
public record HighRiskAlertEvent(Alert alert, Vendor vendor, double score, double threshold) { }
