package com.scm.repository;

import com.scm.model.Vendor;
import java.util.List;

/**
 * IVendorRepository - Repository Interface for Vendors.
 *
 * SOLID: DIP - Services depend on this interface, not MySQL implementations
 * SOLID: ISP - Only vendor-related operations
 * GRASP: Protected Variations - swap DB implementation without touching services
 */
public interface IVendorRepository {
    Vendor findById(int vendorId);
    List<Vendor> findAll();
    List<Vendor> findByStatus(String status);
    int save(Vendor vendor);
    void update(Vendor vendor);
    void softDelete(int vendorId);
    void updateStatus(int vendorId, String status);
    void updateRiskScore(int vendorId, double riskScore);
}
