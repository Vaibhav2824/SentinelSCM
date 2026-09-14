package com.sentinelscm.domain.user;

import com.sentinelscm.domain.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Abstract user. One table, five concrete roles (SINGLE_TABLE inheritance on the role column).
 *
 * OOAD: Abstraction + Inheritance + Polymorphism (permissions and description vary per subclass),
 * Liskov Substitution (any subclass works wherever a User is expected).
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 32)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Read-only mirror of the discriminator so callers can ask a user for its role. */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "role", insertable = false, updatable = false, length = 32)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected User() { }

    protected User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /** Polymorphic: each role declares its own capability list. */
    public abstract List<String> permissions();

    /** Polymorphic: human-readable role description. */
    public abstract String description();

    /** Template method over permissions(). */
    public boolean hasPermission(String permission) {
        return permissions().contains(permission);
    }

    /** Subclasses know their role even before the entity is loaded from the DB. */
    public abstract Role role();

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "] " + name + " <" + email + ">";
    }
}
