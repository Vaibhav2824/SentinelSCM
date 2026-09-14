package com.sentinelscm.repository;

import com.sentinelscm.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    List<Recommendation> findByAlertIdOrderByIdAsc(Integer alertId);
}
