package com.sentinelscm.domain.user;

import com.sentinelscm.domain.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("PROCUREMENT_MANAGER")
public class ProcurementManager extends User {

    private static final List<String> PERMISSIONS = List.of("MANAGE_VENDORS", "SUSPEND_VENDOR", "EVALUATE_RISK", "VIEW_ALERTS", "VIEW_REPORTS");

    protected ProcurementManager() { }

    public ProcurementManager(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    @Override public List<String> permissions() { return PERMISSIONS; }
    @Override public String description() { return "Owns vendor relationships, suspensions and procurement reports"; }
    @Override public Role role() { return Role.PROCUREMENT_MANAGER; }
}
