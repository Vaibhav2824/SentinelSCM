package com.sentinelscm.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Alternative-vendor suggestion attached to an alert. */
@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rec_id")
    private Integer id;

    @Column(name = "alert_id", nullable = false)
    private Integer alertId;

    @Column(name = "suggested_vendor_id", nullable = false)
    private Integer suggestedVendorId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String reason;

    protected Recommendation() { }

    public Recommendation(Integer alertId, Integer suggestedVendorId, String reason) {
        this.alertId = alertId;
        this.suggestedVendorId = suggestedVendorId;
        this.reason = reason;
    }

    public Integer getId() { return id; }
    public Integer getAlertId() { return alertId; }
    public Integer getSuggestedVendorId() { return suggestedVendorId; }
    public String getReason() { return reason; }
}
