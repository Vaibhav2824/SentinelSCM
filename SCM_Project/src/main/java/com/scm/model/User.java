package com.scm.model;

import java.util.List;

/**
 * User - Abstract base class demonstrating OOAD concepts:
 *
 * Abstraction: User is abstract - cannot be instantiated directly
 * Encapsulation: private fields with protected/public accessors
 * Inheritance: 5 concrete subclasses (Administrator, ProcurementManager, RiskAnalyst, WarehouseManager, VendorUser)
 * Polymorphism: getPermissions() and getDescription() are overridden in each subclass
 *
 * GRASP: Information Expert - User knows its own permissions
 * SOLID: LSP - all subclasses can substitute for User
 * SOLID: OCP - new roles can be added by extending User without modifying existing code
 */
public abstract class User {

    // Encapsulation: private fields
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private Role role;

    // Constructor
    public User() {}

    public User(int userId, String name, String email, String passwordHash, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Abstraction: abstract methods that MUST be implemented by subclasses
    /** Returns the list of permission strings for this role. (Polymorphism) */
    public abstract List<String> getPermissions();

    /** Returns a human-readable description of this user's role. (Polymorphism) */
    public abstract String getDescription();

    /** Template method: checks if this user has a specific permission. (Information Expert) */
    public boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    // Getters and Setters (Encapsulation)
    public int getUserId()             { return userId; }
    public void setUserId(int id)      { this.userId = id; }

    public String getName()            { return name; }
    public void setName(String name)   { this.name = name; }

    public String getEmail()           { return email; }
    public void setEmail(String e)     { this.email = e; }

    public String getPasswordHash()    { return passwordHash; }
    public void setPasswordHash(String h) { this.passwordHash = h; }

    public Role getRole()              { return role; }
    public void setRole(Role r)        { this.role = r; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + userId + "] " + name + " <" + email + ">";
    }
}
