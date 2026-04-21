package com.scm.model.user;

import com.scm.model.Role;
import com.scm.model.User;
import java.util.List;

/** ProcurementManager - manages vendor relationships and purchase orders. */
public class ProcurementManager extends User {

    public ProcurementManager() { setRole(Role.PROCUREMENT_MANAGER); }

    public ProcurementManager(int userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.PROCUREMENT_MANAGER);
    }

    @Override
    public List<String> getPermissions() {
        return List.of(
            "VIEW_VENDORS", "CREATE_VENDOR", "EDIT_VENDOR",
            "SUSPEND_VENDOR", "ACTIVATE_VENDOR",
            "VIEW_RISKS", "VIEW_ALERTS", "RESOLVE_ALERT",
            "VIEW_REPORTS", "VIEW_PERFORMANCE_REPORT"
        );
    }

    @Override
    public String getDescription() {
        return "Procurement Manager - Manages vendor relationships and procurement operations.";
    }
}
