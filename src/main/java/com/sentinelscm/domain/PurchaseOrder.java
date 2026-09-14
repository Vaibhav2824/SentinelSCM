package com.sentinelscm.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_id")
    private Integer id;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 32)
    private PoStatus status = PoStatus.PENDING;

    protected PurchaseOrder() { }

    public PurchaseOrder(Integer vendorId, int quantity, LocalDate orderDate, PoStatus status) {
        this.vendorId = vendorId;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Integer getId() { return id; }
    public Integer getVendorId() { return vendorId; }
    public int getQuantity() { return quantity; }
    public LocalDate getOrderDate() { return orderDate; }
    public PoStatus getStatus() { return status; }
}
