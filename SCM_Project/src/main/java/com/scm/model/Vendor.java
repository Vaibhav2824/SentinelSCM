package com.scm.model;

/**
 * Vendor - Core entity model.
 *
 * Encapsulation: private fields + getters/setters
 * Composition: Vendor "has" RiskScore and PurchaseOrder objects
 * GRASP: Information Expert - Vendor knows its own status/risk
 */
public class Vendor {

    private int vendorId;
    private String name;
    private String contact;
    private double rating;
    private double riskScore;
    private String status;
    private Integer userId;

    public Vendor() {}

    public Vendor(int vendorId, String name, String contact, double rating, double riskScore, String status, Integer userId) {
        this.vendorId = vendorId;
        this.name = name;
        this.contact = contact;
        this.rating = rating;
        this.riskScore = riskScore;
        this.status = status;
        this.userId = userId;
    }

    /**
     * Update vendor performance metrics and auto-determine status.
     * Polymorphism: called via Vendor interface, behavior varies by risk score.
     */
    public void updatePerformance(double newRating, double newRiskScore) {
        this.rating = newRating;
        this.riskScore = newRiskScore;
        // Auto-update status based on risk score
        if (newRiskScore > 0.7) {
            this.status = "HIGH_RISK";
        } else if (!"SUSPENDED".equals(this.status) && !"BLACKLISTED".equals(this.status)) {
            this.status = "ACTIVE";
        }
    }

    /** Check if vendor is operational (not suspended/blacklisted/inactive). */
    public boolean isOperational() {
        return "ACTIVE".equals(status) || "HIGH_RISK".equals(status) || "PENDING".equals(status);
    }

    /** Get risk level label based on score. */
    public String getRiskLevel() {
        if (riskScore > 0.7)  return "HIGH";
        if (riskScore > 0.4)  return "MEDIUM";
        return "LOW";
    }

    // Getters and Setters (Encapsulation)
    public int getVendorId()              { return vendorId; }
    public void setVendorId(int id)       { this.vendorId = id; }

    public String getName()               { return name; }
    public void setName(String n)         { this.name = n; }

    public String getContact()            { return contact; }
    public void setContact(String c)      { this.contact = c; }

    public double getRating()             { return rating; }
    public void setRating(double r)       { this.rating = r; }

    public double getRiskScore()          { return riskScore; }
    public void setRiskScore(double rs)   { this.riskScore = rs; }

    public String getStatus()             { return status; }
    public void setStatus(String s)       { this.status = s; }

    public Integer getUserId()            { return userId; }
    public void setUserId(Integer uid)    { this.userId = uid; }

    @Override
    public String toString() {
        return "Vendor[" + vendorId + "] " + name + " | Risk: " + riskScore + " | " + status;
    }
}
