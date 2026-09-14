package com.sentinelscm.repository;

import com.sentinelscm.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    List<PurchaseOrder> findByVendorIdOrderByOrderDateDesc(Integer vendorId);
}
