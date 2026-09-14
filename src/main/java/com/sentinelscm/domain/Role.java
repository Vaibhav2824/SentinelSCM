package com.sentinelscm.domain;

/** Application roles. Names double as Spring Security authorities with the ROLE_ prefix. */
public enum Role {
    ADMIN("Administrator"),
    PROCUREMENT_MANAGER("Procurement Manager"),
    RISK_ANALYST("Risk Analyst"),
    WAREHOUSE_MANAGER("Warehouse Manager"),
    VENDOR("Vendor");

    private final String label;

    Role(String label) { this.label = label; }

    public String label() { return label; }

    public String authority() { return "ROLE_" + name(); }
}
