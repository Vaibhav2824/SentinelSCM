package com.sentinelscm.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer: writes every raised alert to the application log. */
@Component
public class AlertLogListener {

    private static final Logger log = LoggerFactory.getLogger(AlertLogListener.class);

    @EventListener
    public void onHighRisk(HighRiskAlertEvent event) {
        log.warn("ALERT #{} [{}] vendor={} score={} threshold={}",
            event.alert().getId(), event.alert().getSeverity(),
            event.vendor().getName(), event.score(), event.threshold());
    }
}
