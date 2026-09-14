package com.sentinelscm.service;

import com.sentinelscm.domain.Vendor;
import com.sentinelscm.domain.VendorStatus;
import com.sentinelscm.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Vendor lifecycle: CRUD plus the suspend / activate / blacklist state changes. */
@Service
@Transactional
public class VendorService {

    private final VendorRepository vendors;

    public VendorService(VendorRepository vendors) {
        this.vendors = vendors;
    }

    @Transactional(readOnly = true)
    public List<Vendor> listVisible() {
        return vendors.findByStatusNotOrderByIdAsc(VendorStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    public Vendor get(Integer id) {
        return vendors.findById(id).orElseThrow(() -> new NotFoundException("Vendor", id));
    }

    public Vendor create(VendorCommand cmd) {
        validate(cmd);
        return vendors.save(new Vendor(cmd.name().trim(), cmd.contact(), cmd.rating(), 0.0, VendorStatus.PENDING));
    }

    public Vendor update(Integer id, VendorCommand cmd) {
        validate(cmd);
        Vendor v = get(id);
        v.setName(cmd.name().trim());
        v.setContact(cmd.contact());
        v.setRating(cmd.rating());
        return v;
    }

    public void softDelete(Integer id) { get(id).deactivate(); }
    public void suspend(Integer id)    { get(id).suspend(); }
    public void activate(Integer id)   { get(id).activate(); }
    public void blacklist(Integer id)  { get(id).blacklist(); }

    private static void validate(VendorCommand cmd) {
        if (cmd.name() == null || cmd.name().isBlank()) throw new IllegalArgumentException("Vendor name is required");
        if (cmd.rating() < 0 || cmd.rating() > 5) throw new IllegalArgumentException("Rating must be between 0 and 5");
    }
}
