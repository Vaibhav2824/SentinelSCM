package com.scm.model.user;

import com.scm.model.Role;
import com.scm.model.User;
import java.util.List;

/** VendorUser - vendor representative with view-only access. */
public class VendorUser extends User {

    public VendorUser() { setRole(Role.VENDOR); }

    public VendorUser(int userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.VENDOR);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("VIEW_VENDORS", "VIEW_RISKS");
    }

    @Override
    public String getDescription() {
        return "Vendor User - View-only access to vendor performance and risk data.";
    }
}
