package com.sentinelscm.security;

/**
 * Single source of truth for the role matrix, as @PreAuthorize expressions.
 * Shared by the JSON API and the Thymeleaf controllers so the two can never drift.
 */
public final class Access {

    private Access() { }

    public static final String ANY_USER          = "isAuthenticated()";
    public static final String ADMIN             = "hasRole('ADMIN')";
    public static final String ADMIN_PM          = "hasAnyRole('ADMIN','PROCUREMENT_MANAGER')";
    public static final String ADMIN_RA          = "hasAnyRole('ADMIN','RISK_ANALYST')";
    public static final String ADMIN_WM          = "hasAnyRole('ADMIN','WAREHOUSE_MANAGER')";
    public static final String ADMIN_PM_RA       = "hasAnyRole('ADMIN','PROCUREMENT_MANAGER','RISK_ANALYST')";
    public static final String ADMIN_PM_WM       = "hasAnyRole('ADMIN','PROCUREMENT_MANAGER','WAREHOUSE_MANAGER')";
    public static final String ADMIN_PM_RA_VENDOR = "hasAnyRole('ADMIN','PROCUREMENT_MANAGER','RISK_ANALYST','VENDOR')";
}
