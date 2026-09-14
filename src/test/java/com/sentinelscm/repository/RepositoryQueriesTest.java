package com.sentinelscm.repository;

import com.sentinelscm.domain.*;
import com.sentinelscm.domain.user.Administrator;
import com.sentinelscm.domain.user.User;
import com.sentinelscm.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Derived-query contracts against the real MySQL schema and demo seed. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryQueriesTest extends AbstractIntegrationTest {

    @Autowired VendorRepository vendors;
    @Autowired UserRepository users;
    @Autowired EvaluationCriteriaRepository criteria;
    @Autowired RiskScoreRepository riskScores;
    @Autowired AlertRepository alerts;
    @Autowired RecommendationRepository recommendations;
    @Autowired InventoryItemRepository inventory;
    @Autowired RiskRuleRepository riskRules;

    @Test
    void visibleVendorsExcludeSoftDeletedOnes() {
        Vendor ghost = new Vendor("Ghost Corp", "x@ghost.io", 1.0, 0.1, VendorStatus.INACTIVE);
        vendors.save(ghost);

        List<Vendor> visible = vendors.findByStatusNotOrderByIdAsc(VendorStatus.INACTIVE);

        assertThat(visible).hasSize(12);
        assertThat(visible).extracting(Vendor::getName).doesNotContain("Ghost Corp");
        assertThat(visible).extracting(Vendor::getId).isSorted();
    }

    @Test
    void userLookupIsCaseInsensitiveAndPolymorphic() {
        User admin = users.findByEmailIgnoreCase("ADMIN@scm.com").orElseThrow();

        assertThat(admin).isInstanceOf(Administrator.class);
        assertThat(admin.role()).isEqualTo(Role.ADMIN);
        assertThat(admin.hasPermission("CONFIGURE_RISK_RULES")).isTrue();
    }

    @Test
    void latestCriteriaWinsForVendor() {
        EvaluationCriteria latest = criteria.findTopByVendorIdOrderByEvaluatedDateDescIdDesc(1).orElseThrow();

        assertThat(latest.getDeliveryTimeliness()).isEqualTo(0.92);
        assertThat(latest.getDefectRate()).isEqualTo(0.05);
    }

    @Test
    void riskHistoryIsNewestFirst() {
        List<RiskScore> history = riskScores.findByVendorIdOrderByCalculatedDateDesc(2);

        assertThat(history).hasSize(4);
        assertThat(history.get(0).getScore()).isEqualTo(0.78);
        assertThat(history.get(3).getScore()).isEqualTo(0.55);
    }

    @Test
    void unresolvedAlertCountMatchesSeed() {
        assertThat(alerts.countByResolvedFalse()).isEqualTo(5);
        assertThat(alerts.findByResolvedFalseOrderByCreatedAtDesc().get(0).getVendorId()).isEqualTo(9);
    }

    @Test
    void recommendationsAreGroupedByAlert() {
        assertThat(recommendations.findByAlertIdOrderByIdAsc(1)).hasSize(3);
        assertThat(recommendations.findByAlertIdOrderByIdAsc(99)).isEmpty();
    }

    @Test
    void lowStockCountUsesDomainThreshold() {
        assertThat(inventory.countByQuantityLessThan(InventoryItem.LOW_STOCK_THRESHOLD)).isEqualTo(4);
    }

    @Test
    void latestRiskRuleHoldsSeededThreshold() {
        assertThat(riskRules.findTopByOrderByIdDesc().orElseThrow().getThreshold()).isEqualTo(0.7);
    }
}
