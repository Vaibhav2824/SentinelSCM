package com.scm.service;

import com.scm.model.Alert;

/**
 * AlertObserver - Observer Pattern Interface.
 *
 * Design Pattern: Observer (Observer role)
 * SOLID: ISP - single method interface
 * GRASP: Indirection - decouples alert producers from consumers
 */
public interface AlertObserver {
    void onAlert(Alert alert);
}
