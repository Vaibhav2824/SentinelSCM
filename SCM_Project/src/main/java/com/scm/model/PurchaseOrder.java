package com.scm.model;

/** PurchaseOrder - represents a purchase order from a vendor. */
public class PurchaseOrder {
    private int poId;
    private int vendorId;
    private int quantity;
    private String date;
    private String status; // PENDING, DELIVERED, DELAYED, CANCELLED

    public PurchaseOrder() {}
    public PurchaseOrder(int poId, int vendorId, int quantity, String date, String status) {
        this.poId = poId; this.vendorId = vendorId;
        this.quantity = quantity; this.date = date; this.status = status;
    }

    public int getPoId()                { return poId; }
    public void setPoId(int id)         { this.poId = id; }
    public int getVendorId()            { return vendorId; }
    public void setVendorId(int id)     { this.vendorId = id; }
    public int getQuantity()            { return quantity; }
    public void setQuantity(int q)      { this.quantity = q; }
    public String getDate()             { return date; }
    public void setDate(String d)       { this.date = d; }
    public String getStatus()           { return status; }
    public void setStatus(String s)     { this.status = s; }
}
