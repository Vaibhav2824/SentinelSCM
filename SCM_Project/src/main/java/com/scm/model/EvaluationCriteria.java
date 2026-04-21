package com.scm.model;

/** EvaluationCriteria - input to the Strategy pattern risk calculation. */
public class EvaluationCriteria {
    private int criteriaId;
    private int vendorId;
    private double deliveryTimeliness;  // 0.0 to 1.0 (1.0 = perfect)
    private double defectRate;          // 0.0 to 1.0 (0.0 = no defects)
    private double complianceScore;     // 0.0 to 1.0 (1.0 = fully compliant)
    private String evaluatedDate;

    public EvaluationCriteria() {}
    public EvaluationCriteria(int criteriaId, int vendorId, double deliveryTimeliness,
                               double defectRate, double complianceScore, String evaluatedDate) {
        this.criteriaId = criteriaId; this.vendorId = vendorId;
        this.deliveryTimeliness = deliveryTimeliness; this.defectRate = defectRate;
        this.complianceScore = complianceScore; this.evaluatedDate = evaluatedDate;
    }

    public int getCriteriaId()                    { return criteriaId; }
    public void setCriteriaId(int id)             { this.criteriaId = id; }
    public int getVendorId()                      { return vendorId; }
    public void setVendorId(int id)               { this.vendorId = id; }
    public double getDeliveryTimeliness()          { return deliveryTimeliness; }
    public void setDeliveryTimeliness(double d)   { this.deliveryTimeliness = d; }
    public double getDefectRate()                  { return defectRate; }
    public void setDefectRate(double d)            { this.defectRate = d; }
    public double getComplianceScore()             { return complianceScore; }
    public void setComplianceScore(double c)       { this.complianceScore = c; }
    public String getEvaluatedDate()               { return evaluatedDate; }
    public void setEvaluatedDate(String d)         { this.evaluatedDate = d; }
}
