package com.sentinelscm.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/** Risk alert raised for a vendor. Created via AlertFactory, broadcast via Spring events. */
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Integer id;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 16)
    private Severity severity = Severity.MEDIUM;

    @Column(nullable = false)
    private boolean resolved;

    protected Alert() { }

    public Alert(Integer vendorId, String message, Severity severity) {
        this.vendorId = vendorId;
        this.message = message;
        this.severity = severity;
    }

    public void resolve() { this.resolved = true; }

    public Integer getId() { return id; }
    public Integer getVendorId() { return vendorId; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Severity getSeverity() { return severity; }
    public boolean isResolved() { return resolved; }
}
