package com.scm.service;

import com.scm.model.Alert;

/**
 * ConsoleLogObserver - Concrete Observer that logs alerts to console.
 *
 * Design Pattern: Observer (ConcreteObserver)
 * SOLID: OCP - Adding this observer required no changes to AlertService
 */
public class ConsoleLogObserver implements AlertObserver {

    @Override
    public void onAlert(Alert alert) {
        System.out.printf("[ALERT] [%s] Vendor#%d — %s%n",
            alert.getSeverity(), alert.getVendorId(), alert.getMessage());
    }
}
