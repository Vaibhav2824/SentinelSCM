package com.scm.model;

/**
 * Role - Enum for all system roles.
 *
 * SOLID: ISP - each role has only the permissions it needs
 * GRASP: Information Expert - role knows its own string representation
 */
public enum Role {
    ADMIN,
    PROCUREMENT_MANAGER,
    RISK_ANALYST,
    WAREHOUSE_MANAGER,
    VENDOR;

    public String displayName() {
        return switch (this) {
            case ADMIN -> "Administrator";
            case PROCUREMENT_MANAGER -> "Procurement Manager";
            case RISK_ANALYST -> "Risk Analyst";
            case WAREHOUSE_MANAGER -> "Warehouse Manager";
            case VENDOR -> "Vendor User";
        };
    }
}
