package com.scm.service;

import com.scm.model.Recommendation;
import com.scm.model.Vendor;
import com.scm.repository.IAlertRepository;
import com.scm.repository.IVendorRepository;
import java.util.List;

/**
 * RecommendationService - Pure Fabrication pattern.
 *
 * GRASP: Pure Fabrication - does not map to any domain entity
 *   but provides a service (finding alternative vendors) that no domain class should own.
 * GRASP: Low Coupling - only depends on interfaces
 * SOLID: SRP - single responsibility: generate vendor recommendations
 */
public class RecommendationService {

    private final IVendorRepository vendorRepo;
    private final IAlertRepository alertRepo;

    public RecommendationService(IVendorRepository vendorRepo, IAlertRepository alertRepo) {
        this.vendorRepo = vendorRepo;
        this.alertRepo = alertRepo;
    }

    /**
     * Find low-risk alternative vendors for a high-risk vendor.
     * Saves recommendations to the alert.
     * Pure Fabrication: artificial class to support this capability.
     */
    public void suggestAlternatives(Vendor riskVendor, int alertId) {
        List<Vendor> allVendors = vendorRepo.findAll();

        int saved = 0;
        for (Vendor candidate : allVendors) {
            if (candidate.getVendorId() == riskVendor.getVendorId()) continue;
            if (!"ACTIVE".equals(candidate.getStatus())) continue;
            if (candidate.getRiskScore() >= 0.4) continue;  // only recommend low-risk vendors
            if (saved >= 3) break;

            Recommendation rec = new Recommendation();
            rec.setAlertId(alertId);
            rec.setSuggestedVendorId(candidate.getVendorId());
            rec.setReason(String.format(
                "%s — risk score %.2f, rating %.1f★, status: %s. Lower risk alternative.",
                candidate.getName(), candidate.getRiskScore(), candidate.getRating(), candidate.getStatus()
            ));
            alertRepo.saveRecommendation(rec);
            saved++;
        }

        System.out.printf("[RecommendationService] Generated %d recommendation(s) for alert #%d%n", saved, alertId);
    }
}
