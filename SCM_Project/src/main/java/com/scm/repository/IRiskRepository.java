package com.scm.repository;

import com.scm.model.EvaluationCriteria;
import com.scm.model.RiskScore;
import java.util.List;

/** IRiskRepository - interface for risk data access. */
public interface IRiskRepository {
    EvaluationCriteria findLatestCriteria(int vendorId);
    List<EvaluationCriteria> findCriteriaHistory(int vendorId);
    void saveCriteria(EvaluationCriteria criteria);
    void saveRiskScore(RiskScore riskScore);
    List<RiskScore> findRiskHistory(int vendorId);
    RiskScore findLatestRiskScore(int vendorId);
    double getThreshold();
}
