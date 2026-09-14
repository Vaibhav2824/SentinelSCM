package com.sentinelscm.repository;

import com.sentinelscm.domain.Vendor;
import com.sentinelscm.domain.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    /** "Visible" vendors: everything except soft-deleted ones. */
    List<Vendor> findByStatusNotOrderByIdAsc(VendorStatus excluded);
    List<Vendor> findByStatusOrderByIdAsc(VendorStatus status);
    long countByStatus(VendorStatus status);
}
