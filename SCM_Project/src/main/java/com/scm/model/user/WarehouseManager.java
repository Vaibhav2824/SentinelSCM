package com.scm.model.user;

import com.scm.model.Role;
import com.scm.model.User;
import java.util.List;

/** WarehouseManager - manages inventory and stock levels. */
public class WarehouseManager extends User {

    public WarehouseManager() { setRole(Role.WAREHOUSE_MANAGER); }

    public WarehouseManager(int userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.WAREHOUSE_MANAGER);
    }

    @Override
    public List<String> getPermissions() {
        return List.of("VIEW_INVENTORY", "EDIT_INVENTORY", "VIEW_VENDORS");
    }

    @Override
    public String getDescription() {
        return "Warehouse Manager - Manages inventory stock levels and warehouse operations.";
    }
}
