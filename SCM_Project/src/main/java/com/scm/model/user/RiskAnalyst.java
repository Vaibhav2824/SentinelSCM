package com.scm.model.user;

import com.scm.model.Role;
import com.scm.model.User;
import java.util.List;

/** RiskAnalyst - evaluates vendor risk and manages risk scores. */
public class RiskAnalyst extends User {

    public RiskAnalyst() { setRole(Role.RISK_ANALYST); }

    public RiskAnalyst(int userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.RISK_ANALYST);
    }

    @Override
    public List<String> getPermissions() {
        return List.of(
            "VIEW_VENDORS", "VIEW_RISKS",
            "CALCULATE_RISK", "EVALUATE_VENDOR",
            "VIEW_ALERTS", "RESOLVE_ALERT",
            "VIEW_REPORTS", "VIEW_PERFORMANCE_REPORT"
        );
    }

    @Override
    public String getDescription() {
        return "Risk Analyst - Evaluates vendor performance and calculates risk scores.";
    }
}
