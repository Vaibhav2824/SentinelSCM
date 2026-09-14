package com.sentinelscm.event;

import com.sentinelscm.service.RecommendationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer: on every high-risk alert, attach alternative-vendor recommendations. */
@Component
public class RecommendationListener {

    private final RecommendationService recommendationService;

    public RecommendationListener(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @EventListener
    public void onHighRisk(HighRiskAlertEvent event) {
        recommendationService.suggestAlternatives(event.vendor(), event.alert().getId());
    }
}
