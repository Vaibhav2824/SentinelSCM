package com.sentinelscm.factory;

import com.sentinelscm.domain.Alert;
import com.sentinelscm.domain.Severity;
import com.sentinelscm.domain.Vendor;
import com.sentinelscm.domain.VendorStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlertFactoryTest {

    @Test
    void highRiskAlertIsSevereAndNamesTheVendorAndNumbers() {
        Vendor vendor = new Vendor("FastSupply Co", "orders@fastsupply.com", 2.1, 0.78, VendorStatus.ACTIVE);

        Alert alert = AlertFactory.highRiskAlert(vendor, 0.784, 0.7);

        assertThat(alert.getSeverity()).isEqualTo(Severity.HIGH);
        assertThat(alert.isResolved()).isFalse();
        assertThat(alert.getMessage())
            .contains("FastSupply Co")
            .contains("0.78")
            .contains("0.70");
    }
}
