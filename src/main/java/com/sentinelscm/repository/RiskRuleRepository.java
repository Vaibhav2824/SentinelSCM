package com.sentinelscm.repository;

import com.sentinelscm.domain.RiskRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskRuleRepository extends JpaRepository<RiskRule, Integer> {
    Optional<RiskRule> findTopByOrderByIdDesc();
}
