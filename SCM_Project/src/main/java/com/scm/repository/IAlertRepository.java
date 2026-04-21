package com.scm.repository;

import com.scm.model.Alert;
import com.scm.model.Recommendation;
import java.util.List;

/** IAlertRepository - interface for alert and recommendation data access. */
public interface IAlertRepository {
    List<Alert> findAll();
    List<Alert> findUnresolved();
    Alert findById(int alertId);
    int saveAlert(Alert alert);
    void resolveAlert(int alertId);
    List<Recommendation> findRecommendationsByAlertId(int alertId);
    void saveRecommendation(Recommendation rec);
}
