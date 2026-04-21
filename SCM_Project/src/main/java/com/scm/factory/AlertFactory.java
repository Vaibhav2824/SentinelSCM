package com.scm.factory;

import com.scm.model.Alert;
import com.scm.model.Vendor;

/**
 * AlertFactory - Factory Pattern for creating Alert objects.
 *
 * Design Pattern: Factory Method
 * GRASP: Creator - AlertFactory is responsible for creating Alert instances
 * SOLID: SRP - only creates alerts; no other responsibility
 * SOLID: OCP - new alert types can be added (e.g., HighRiskAlertFactory) without modification
 */
public class AlertFactory {

    /**
     * Creates an Alert for a vendor that exceeds the risk threshold.
     * Factory Method: encapsulates Alert construction logic.
     */
    public static Alert createAlert(Vendor vendor, double riskScore) {
        String severity = determineSeverity(riskScore);
        String message = buildMessage(vendor, riskScore, severity);

        Alert alert = new Alert();
        alert.setVendorId(vendor.getVendorId());
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setResolved(false);

        System.out.printf("[AlertFactory] Created %s alert for vendor: %s (score=%.3f)%n",
            severity, vendor.getName(), riskScore);

        return alert;
    }

    /** Determine severity level from score. */
    private static String determineSeverity(double score) {
        if (score >= 0.85) return "HIGH";
        if (score >= 0.7)  return "HIGH";
        if (score >= 0.5)  return "MEDIUM";
        return "LOW";
    }

    /** Build descriptive alert message. */
    private static String buildMessage(Vendor vendor, double score, String severity) {
        return String.format(
            "%s: %s risk score (%.3f) exceeds threshold (0.700). " +
            "Immediate review required. Current status: %s.",
            severity.equals("HIGH") ? "HIGH RISK" : "WARNING",
            vendor.getName(), score, vendor.getStatus()
        );
    }
}
