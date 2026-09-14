package com.sentinelscm.domain.user;

import com.sentinelscm.domain.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("RISK_ANALYST")
public class RiskAnalyst extends User {

    private static final List<String> PERMISSIONS = List.of("EVALUATE_RISK", "CALCULATE_RISK", "VIEW_ALERTS", "VIEW_REPORTS");

    protected RiskAnalyst() { }

    public RiskAnalyst(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    @Override public List<String> permissions() { return PERMISSIONS; }
    @Override public String description() { return "Evaluates vendors and drives risk recalculation"; }
    @Override public Role role() { return Role.RISK_ANALYST; }
}
