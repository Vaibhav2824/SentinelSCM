package com.sentinelscm.repository;

import com.sentinelscm.domain.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskScoreRepository extends JpaRepository<RiskScore, Integer> {
    List<RiskScore> findByVendorIdOrderByCalculatedDateDesc(Integer vendorId);
}
