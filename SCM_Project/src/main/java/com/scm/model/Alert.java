package com.scm.model;

/** Alert - created by AlertFactory when risk exceeds threshold. */
public class Alert {
    private int alertId;
    private int vendorId;
    private String message;
    private String timestamp;
    private String severity;   // LOW, MEDIUM, HIGH
    private boolean resolved;

    public Alert() {}
    public Alert(int alertId, int vendorId, String message, String timestamp, String severity, boolean resolved) {
        this.alertId = alertId; this.vendorId = vendorId; this.message = message;
        this.timestamp = timestamp; this.severity = severity; this.resolved = resolved;
    }

    public int getAlertId()               { return alertId; }
    public void setAlertId(int id)        { this.alertId = id; }
    public int getVendorId()              { return vendorId; }
    public void setVendorId(int id)       { this.vendorId = id; }
    public String getMessage()            { return message; }
    public void setMessage(String m)      { this.message = m; }
    public String getTimestamp()          { return timestamp; }
    public void setTimestamp(String t)    { this.timestamp = t; }
    public String getSeverity()           { return severity; }
    public void setSeverity(String s)     { this.severity = s; }
    public boolean isResolved()           { return resolved; }
    public void setResolved(boolean r)    { this.resolved = r; }

    // Alias for JSON serialization compatibility
    public int getId()                    { return alertId; }
}
