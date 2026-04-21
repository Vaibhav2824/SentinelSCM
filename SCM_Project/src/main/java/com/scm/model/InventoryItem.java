package com.scm.model;

/** InventoryItem - represents a stock item in the warehouse. */
public class InventoryItem {
    private int itemId;
    private String itemName;
    private int quantity;
    private String lastUpdated;

    public InventoryItem() {}
    public InventoryItem(int itemId, String itemName, int quantity, String lastUpdated) {
        this.itemId = itemId; this.itemName = itemName;
        this.quantity = quantity; this.lastUpdated = lastUpdated;
    }

    public int getItemId()                  { return itemId; }
    public void setItemId(int id)           { this.itemId = id; }
    public String getItemName()             { return itemName; }
    public void setItemName(String n)       { this.itemName = n; }
    public int getQuantity()                { return quantity; }
    public void setQuantity(int q)          { this.quantity = q; }
    public String getLastUpdated()          { return lastUpdated; }
    public void setLastUpdated(String d)    { this.lastUpdated = d; }
    public boolean isLowStock()             { return quantity < 50; }

    public int getId()                      { return itemId; }
    public String getName()                 { return itemName; }
}
