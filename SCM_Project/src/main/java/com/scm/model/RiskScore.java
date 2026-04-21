package com.scm.model;

/** RiskScore - represents a calculated risk score for a vendor at a point in time. */
public class RiskScore {
    private int scoreId;
    private int vendorId;
    private double score;
    private String calculatedDate;

    public RiskScore() {}
    public RiskScore(int scoreId, int vendorId, double score, String calculatedDate) {
        this.scoreId = scoreId; this.vendorId = vendorId;
        this.score = score; this.calculatedDate = calculatedDate;
    }

    public int getScoreId()                    { return scoreId; }
    public void setScoreId(int id)             { this.scoreId = id; }
    public int getVendorId()                   { return vendorId; }
    public void setVendorId(int id)            { this.vendorId = id; }
    public double getScore()                   { return score; }
    public void setScore(double s)             { this.score = s; }
    public String getCalculatedDate()          { return calculatedDate; }
    public void setCalculatedDate(String d)    { this.calculatedDate = d; }

    /** Categorize risk level. */
    public String getRiskLevel() {
        if (score > 0.7) return "HIGH";
        if (score > 0.4) return "MEDIUM";
        return "LOW";
    }
}
