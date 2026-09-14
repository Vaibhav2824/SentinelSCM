package com.sentinelscm.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Historical risk score entry. */
@Entity
@Table(name = "risk_scores")
public class RiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Integer id;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(nullable = false)
    private double score;

    @CreationTimestamp
    @Column(name = "calculated_date", nullable = false, updatable = false)
    private LocalDateTime calculatedDate;

    protected RiskScore() { }

    public RiskScore(Integer vendorId, double score) {
        this.vendorId = vendorId;
        this.score = score;
    }

    public Integer getId() { return id; }
    public Integer getVendorId() { return vendorId; }
    public double getScore() { return score; }
    public LocalDateTime getCalculatedDate() { return calculatedDate; }
    public RiskLevel level() { return RiskLevel.of(score); }
}
