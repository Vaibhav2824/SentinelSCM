package com.sentinelscm.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

/** Input to the risk Strategy: one evaluation snapshot for a vendor. */
@Entity
@Table(name = "evaluation_criteria")
public class EvaluationCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "criteria_id")
    private Integer id;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(name = "delivery_timeliness", nullable = false)
    private double deliveryTimeliness;

    @Column(name = "defect_rate", nullable = false)
    private double defectRate;

    @Column(name = "compliance_score", nullable = false)
    private double complianceScore;

    @Column(name = "evaluated_date", nullable = false)
    private LocalDate evaluatedDate;

    protected EvaluationCriteria() { }

    public EvaluationCriteria(Integer vendorId, double deliveryTimeliness, double defectRate,
                              double complianceScore, LocalDate evaluatedDate) {
        this.vendorId = vendorId;
        this.deliveryTimeliness = deliveryTimeliness;
        this.defectRate = defectRate;
        this.complianceScore = complianceScore;
        this.evaluatedDate = evaluatedDate;
    }

    public Integer getId() { return id; }
    public Integer getVendorId() { return vendorId; }
    public double getDeliveryTimeliness() { return deliveryTimeliness; }
    public double getDefectRate() { return defectRate; }
    public double getComplianceScore() { return complianceScore; }
    public LocalDate getEvaluatedDate() { return evaluatedDate; }
}
