package com.sentinelscm.repository;

import com.sentinelscm.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    List<Alert> findAllByOrderByCreatedAtDesc();
    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();
    long countByResolvedFalse();
}
