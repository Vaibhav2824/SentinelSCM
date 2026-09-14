package com.sentinelscm.domain.user;

import com.sentinelscm.domain.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {

    private static final List<String> PERMISSIONS = List.of("MANAGE_USERS", "MANAGE_VENDORS", "BLACKLIST_VENDOR", "EVALUATE_RISK", "VIEW_ALERTS", "MANAGE_INVENTORY", "VIEW_REPORTS", "EXPORT_REPORTS", "CONFIGURE_RISK_RULES");

    protected Administrator() { }

    public Administrator(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    @Override public List<String> permissions() { return PERMISSIONS; }
    @Override public String description() { return "Full system access: users, vendors, risk rules, reports"; }
    @Override public Role role() { return Role.ADMIN; }
}
