package com.scm.service;

import com.scm.model.Vendor;
import com.scm.repository.IVendorRepository;
import java.util.List;

/**
 * VendorService - business logic for vendor management.
 *
 * GRASP: Creator - responsible for Vendor lifecycle
 * GRASP: High Cohesion - focused only on vendor business logic
 * GRASP: Low Coupling - depends only on IVendorRepository (interface)
 * SOLID: SRP - only handles vendor-related business rules
 * SOLID: DIP - depends on IVendorRepository abstraction, not MySQL implementation
 */
public class VendorService {

    private final IVendorRepository vendorRepo;

    public VendorService(IVendorRepository vendorRepo) {
        this.vendorRepo = vendorRepo;
    }

    public List<Vendor> getAllVendors() {
        return vendorRepo.findAll();
    }

    public Vendor getVendorById(int id) {
        return vendorRepo.findById(id);
    }

    public int createVendor(Vendor vendor) {
        if (vendor.getName() == null || vendor.getName().isBlank())
            throw new IllegalArgumentException("Vendor name is required");
        if (vendor.getStatus() == null) vendor.setStatus("PENDING");
        return vendorRepo.save(vendor);
    }

    public void updateVendor(Vendor vendor) {
        if (vendor.getName() == null || vendor.getName().isBlank())
            throw new IllegalArgumentException("Vendor name is required");
        vendorRepo.update(vendor);
    }

    public void deleteVendor(int id)      { vendorRepo.softDelete(id); }
    public void suspendVendor(int id)     { vendorRepo.updateStatus(id, "SUSPENDED"); }
    public void blacklistVendor(int id)   { vendorRepo.updateStatus(id, "BLACKLISTED"); }
    public void activateVendor(int id)    { vendorRepo.updateStatus(id, "ACTIVE"); }
}
