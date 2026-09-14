package com.sentinelscm.domain.user;

import com.sentinelscm.domain.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("VENDOR")
public class VendorUser extends User {

    private static final List<String> PERMISSIONS = List.of("VIEW_OWN_PROFILE");

    protected VendorUser() { }

    public VendorUser(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    @Override public List<String> permissions() { return PERMISSIONS; }
    @Override public String description() { return "External vendor with read-only visibility into own standing"; }
    @Override public Role role() { return Role.VENDOR; }
}
