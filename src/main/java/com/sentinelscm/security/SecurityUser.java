package com.sentinelscm.security;

import com.sentinelscm.domain.Role;
import com.sentinelscm.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Adapter from the domain User hierarchy to Spring Security's UserDetails. */
public class SecurityUser implements UserDetails {

    private final Integer id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Role role;

    public SecurityUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.role();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Role getRole() { return role; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.authority()));
    }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
}
