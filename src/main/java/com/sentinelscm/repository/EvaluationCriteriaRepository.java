package com.sentinelscm.repository;

import com.sentinelscm.domain.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Integer> {
    Optional<EvaluationCriteria> findTopByVendorIdOrderByEvaluatedDateDescIdDesc(Integer vendorId);
}
