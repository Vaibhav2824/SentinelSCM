package com.sentinelscm.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/** Warehouse stock line. */
@Entity
@Table(name = "inventory")
public class InventoryItem {

    public static final int LOW_STOCK_THRESHOLD = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    protected InventoryItem() { }

    public InventoryItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public boolean isLowStock() { return quantity < LOW_STOCK_THRESHOLD; }

    public void updateQuantity(int newQuantity) {
        if (newQuantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = newQuantity;
    }

    public Integer getId() { return id; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}
