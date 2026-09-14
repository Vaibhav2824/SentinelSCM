package com.sentinelscm.factory;

import com.sentinelscm.domain.Alert;
import com.sentinelscm.domain.Severity;
import com.sentinelscm.domain.Vendor;

/**
 * Factory pattern: the single place that knows how to word and grade risk alerts.
 * Callers never construct Alert instances for risk breaches directly.
 */
public final class AlertFactory {

    private AlertFactory() { }

    /** Alert for a vendor whose fresh score crossed the configured threshold. */
    public static Alert highRiskAlert(Vendor vendor, double score, double threshold) {
        String message = String.format(
            "HIGH RISK: %s risk score (%.2f) exceeds threshold (%.2f). Review vendor and consider alternatives.",
            vendor.getName(), score, threshold);
        return new Alert(vendor.getId(), message, Severity.HIGH);
    }
}
