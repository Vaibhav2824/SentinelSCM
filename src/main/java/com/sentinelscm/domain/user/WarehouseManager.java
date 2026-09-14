package com.sentinelscm.domain.user;

import com.sentinelscm.domain.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("WAREHOUSE_MANAGER")
public class WarehouseManager extends User {

    private static final List<String> PERMISSIONS = List.of("MANAGE_INVENTORY");

    protected WarehouseManager() { }

    public WarehouseManager(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    @Override public List<String> permissions() { return PERMISSIONS; }
    @Override public String description() { return "Maintains stock levels and inventory health"; }
    @Override public Role role() { return Role.WAREHOUSE_MANAGER; }
}
