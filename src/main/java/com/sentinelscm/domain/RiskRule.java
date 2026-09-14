package com.sentinelscm.domain;

import jakarta.persistence.*;

/** Runtime-configurable alert threshold (GRASP Protected Variations). Latest row wins. */
@Entity
@Table(name = "risk_rules")
public class RiskRule {

    public static final double DEFAULT_THRESHOLD = 0.7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Integer id;

    @Column(nullable = false)
    private double threshold = DEFAULT_THRESHOLD;

    @Column(name = "condition_text", nullable = false, length = 100)
    private String conditionText = "score > threshold";

    @Column(name = "created_by")
    private Integer createdBy;

    protected RiskRule() { }

    public RiskRule(double threshold, Integer createdBy) {
        if (threshold <= 0 || threshold >= 1) throw new IllegalArgumentException("Threshold must be between 0 and 1 exclusive");
        this.threshold = threshold;
        this.createdBy = createdBy;
    }

    public Integer getId() { return id; }
    public double getThreshold() { return threshold; }
    public String getConditionText() { return conditionText; }
    public Integer getCreatedBy() { return createdBy; }
}
