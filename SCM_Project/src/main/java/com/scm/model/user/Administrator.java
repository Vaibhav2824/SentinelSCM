package com.scm.model.user;

import com.scm.model.Role;
import com.scm.model.User;
import java.util.List;

/**
 * Administrator - Concrete subclass of User.
 * Inheritance + Polymorphism: overrides getPermissions() and getDescription()
 * LSP: can substitute for User in all contexts
 */
public class Administrator extends User {

    public Administrator() { setRole(Role.ADMIN); }

    public Administrator(int userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.ADMIN);
    }

    @Override
    public List<String> getPermissions() {
        return List.of(
            "VIEW_VENDORS", "CREATE_VENDOR", "EDIT_VENDOR", "DELETE_VENDOR",
            "BLACKLIST_VENDOR", "SUSPEND_VENDOR", "ACTIVATE_VENDOR",
            "VIEW_RISKS", "CALCULATE_RISK", "EVALUATE_VENDOR",
            "VIEW_ALERTS", "RESOLVE_ALERT",
            "VIEW_INVENTORY", "EDIT_INVENTORY",
            "VIEW_REPORTS", "VIEW_STRATEGIC_REPORT",
            "MANAGE_USERS", "MANAGE_RULES"
        );
    }

    @Override
    public String getDescription() {
        return "System Administrator - Full access to all modules and system configuration.";
    }
}
