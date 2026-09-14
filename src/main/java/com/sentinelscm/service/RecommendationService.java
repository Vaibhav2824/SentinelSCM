package com.sentinelscm.service;

import com.sentinelscm.domain.Recommendation;
import com.sentinelscm.domain.RiskLevel;
import com.sentinelscm.domain.Vendor;
import com.sentinelscm.domain.VendorStatus;
import com.sentinelscm.repository.RecommendationRepository;
import com.sentinelscm.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * GRASP Pure Fabrication: finding substitute vendors belongs to no domain entity,
 * so it lives in its own service.
 */
@Service
public class RecommendationService {

    static final int MAX_RECOMMENDATIONS = 3;

    private final VendorRepository vendors;
    private final RecommendationRepository recommendations;

    public RecommendationService(VendorRepository vendors, RecommendationRepository recommendations) {
        this.vendors = vendors;
        this.recommendations = recommendations;
    }

    /** Persist up to three low-risk ACTIVE alternatives for the given alert, best first. */
    @Transactional
    public List<Recommendation> suggestAlternatives(Vendor riskVendor, Integer alertId) {
        List<Recommendation> created = vendors.findByStatusOrderByIdAsc(VendorStatus.ACTIVE).stream()
            .filter(v -> !v.getId().equals(riskVendor.getId()))
            .filter(v -> v.riskLevel() == RiskLevel.LOW)
            .sorted(Comparator.comparingDouble(Vendor::getRiskScore))
            .limit(MAX_RECOMMENDATIONS)
            .map(v -> new Recommendation(alertId, v.getId(), reasonFor(v)))
            .toList();
        return recommendations.saveAll(created);
    }

    @Transactional(readOnly = true)
    public List<Recommendation> forAlert(Integer alertId) {
        return recommendations.findByAlertIdOrderByIdAsc(alertId);
    }

    private static String reasonFor(Vendor v) {
        return String.format("%s: risk score %.2f, rating %.1f/5, status %s. Lower-risk alternative.",
            v.getName(), v.getRiskScore(), v.getRating(), v.getStatus());
    }
}
