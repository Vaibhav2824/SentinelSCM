package com.sentinelscm.service;

import com.sentinelscm.domain.Alert;
import com.sentinelscm.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AlertService {

    private final AlertRepository alerts;

    public AlertService(AlertRepository alerts) {
        this.alerts = alerts;
    }

    @Transactional(readOnly = true)
    public List<Alert> listAll() { return alerts.findAllByOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public List<Alert> listUnresolved() { return alerts.findByResolvedFalseOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public long unresolvedCount() { return alerts.countByResolvedFalse(); }

    @Transactional(readOnly = true)
    public Alert get(Integer id) {
        return alerts.findById(id).orElseThrow(() -> new NotFoundException("Alert", id));
    }

    public Alert resolve(Integer id) {
        Alert alert = get(id);
        alert.resolve();
        return alert;
    }
}
