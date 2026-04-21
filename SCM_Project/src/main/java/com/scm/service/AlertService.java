package com.scm.service;

import com.scm.model.Alert;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertService - Observer Pattern Hub.
 *
 * Design Pattern: Observer (Subject/Publisher role)
 * GRASP: Indirection - decouples RiskService from alert consumers
 * GRASP: Low Coupling - observers register themselves; AlertService doesn't know implementations
 * SOLID: OCP - new observers can be added without modifying AlertService
 */
public class AlertService {

    // Registry of observers (Observer pattern)
    private final List<AlertObserver> observers = new ArrayList<>();

    /** Register a new observer. (Observer pattern: subscribe) */
    public void addObserver(AlertObserver observer) {
        observers.add(observer);
        System.out.println("[AlertService] Observer registered: " + observer.getClass().getSimpleName());
    }

    /** Notify all registered observers of a new alert. (Observer pattern: publish) */
    public void fireAlert(Alert alert) {
        System.out.println("[AlertService] Firing alert to " + observers.size() + " observer(s): " + alert.getSeverity());
        for (AlertObserver observer : observers) {
            try {
                observer.onAlert(alert);
            } catch (Exception e) {
                System.err.println("[AlertService] Observer error: " + e.getMessage());
            }
        }
    }
}
