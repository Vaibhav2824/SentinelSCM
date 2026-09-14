package com.sentinelscm.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Vendor aggregate root.
 * GRASP Information Expert: the vendor decides its own status from its risk score.
 */
@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 200)
    private String contact;

    @Column(nullable = false)
    private double rating;

    @Column(name = "risk_score", nullable = false)
    private double riskScore;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 32)
    private VendorStatus status = VendorStatus.PENDING;

    @Column(name = "user_id")
    private Integer userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Vendor() { }

    public Vendor(String name, String contact, double rating, double riskScore, VendorStatus status) {
        this.name = name;
        this.contact = contact;
        this.rating = rating;
        this.riskScore = riskScore;
        this.status = status == null ? VendorStatus.PENDING : status;
    }

    /**
     * Record a fresh risk score and derive status. Suspended and blacklisted vendors keep their
     * manual status; everyone else flips between ACTIVE and HIGH_RISK around the HIGH floor.
     */
    public void updatePerformance(double newRating, double newRiskScore) {
        this.rating = newRating;
        this.riskScore = newRiskScore;
        if (RiskLevel.of(newRiskScore) == RiskLevel.HIGH) {
            this.status = VendorStatus.HIGH_RISK;
        } else if (status != VendorStatus.SUSPENDED && status != VendorStatus.BLACKLISTED) {
            this.status = VendorStatus.ACTIVE;
        }
    }

    public RiskLevel riskLevel() { return RiskLevel.of(riskScore); }

    public boolean isOperational() { return status.isOperational(); }

    public void suspend()    { this.status = VendorStatus.SUSPENDED; }
    public void blacklist()  { this.status = VendorStatus.BLACKLISTED; }
    public void activate()   { this.status = VendorStatus.ACTIVE; }
    public void deactivate() { this.status = VendorStatus.INACTIVE; }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public double getRating() { return rating; }
    public double getRiskScore() { return riskScore; }
    public VendorStatus getStatus() { return status; }
    public Integer getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setRating(double rating) { this.rating = rating; }
    public void setStatus(VendorStatus status) { this.status = status; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Vendor[" + id + "] " + name + " | risk=" + riskScore + " | " + status;
    }
}
