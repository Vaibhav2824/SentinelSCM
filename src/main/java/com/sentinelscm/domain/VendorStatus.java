package com.sentinelscm.domain;

/** Vendor lifecycle states. */
public enum VendorStatus {
    ACTIVE, PENDING, HIGH_RISK, SUSPENDED, BLACKLISTED, INACTIVE;

    /** Operational vendors can still receive orders and recommendations. */
    public boolean isOperational() {
        return this == ACTIVE || this == HIGH_RISK || this == PENDING;
    }
}
