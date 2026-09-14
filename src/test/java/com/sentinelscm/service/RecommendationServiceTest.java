package com.sentinelscm.service;

import com.sentinelscm.domain.Recommendation;
import com.sentinelscm.domain.Vendor;
import com.sentinelscm.domain.VendorStatus;
import com.sentinelscm.repository.RecommendationRepository;
import com.sentinelscm.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock VendorRepository vendors;
    @Mock RecommendationRepository recommendations;
    @InjectMocks RecommendationService service;

    private static Vendor vendor(int id, String name, double risk) {
        Vendor v = new Vendor(name, name + "@x.com", 4.0, risk, VendorStatus.ACTIVE);
        ReflectionTestUtils.setField(v, "id", id);
        return v;
    }

    @Test
    void picksUpToThreeLowestRiskActiveVendorsExcludingTheRiskyOne() {
        Vendor risky = vendor(2, "FastSupply", 0.78);
        when(vendors.findByStatusOrderByIdAsc(VendorStatus.ACTIVE)).thenReturn(List.of(
            vendor(1, "QuickParts", 0.12),
            risky,
            vendor(3, "ReliableGoods", 0.04),
            vendor(4, "BudgetMaterials", 0.52),   // MEDIUM: excluded
            vendor(6, "MetalWorks", 0.18),
            vendor(10, "PrimeMaterials", 0.09)
        ));
        when(recommendations.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Recommendation> result = service.suggestAlternatives(risky, 42);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Recommendation::getSuggestedVendorId).containsExactly(3, 10, 1);
        assertThat(result).allSatisfy(r -> {
            assertThat(r.getAlertId()).isEqualTo(42);
            assertThat(r.getReason()).contains("Lower-risk alternative");
        });
    }

    @Test
    void returnsEmptyWhenNoLowRiskAlternativeExists() {
        Vendor risky = vendor(2, "FastSupply", 0.78);
        when(vendors.findByStatusOrderByIdAsc(VendorStatus.ACTIVE)).thenReturn(List.of(risky, vendor(4, "Budget", 0.52)));
        when(recommendations.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.suggestAlternatives(risky, 7)).isEmpty();
    }
}
