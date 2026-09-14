package com.sentinelscm.web;

import com.sentinelscm.api.dto.AlertDto;
import com.sentinelscm.domain.RiskLevel;
import com.sentinelscm.domain.Vendor;
import com.sentinelscm.domain.VendorStatus;
import com.sentinelscm.security.Access;
import com.sentinelscm.security.SecurityUser;
import com.sentinelscm.service.AlertService;
import com.sentinelscm.service.InventoryService;
import com.sentinelscm.service.VendorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private static final int FEED_SIZE = 5;
    private static final int TOP_RISK_BARS = 7;

    private final VendorService vendors;
    private final AlertService alerts;
    private final InventoryService inventory;

    public DashboardController(VendorService vendors, AlertService alerts, InventoryService inventory) {
        this.vendors = vendors;
        this.alerts = alerts;
        this.inventory = inventory;
    }

    @GetMapping("/dashboard")
    @PreAuthorize(Access.ANY_USER)
    public String dashboard(@AuthenticationPrincipal SecurityUser user, Model model) {
        List<Vendor> all = vendors.listVisible();
        Map<Integer, String> names = all.stream().collect(Collectors.toMap(Vendor::getId, Vendor::getName));

        model.addAttribute("totalVendors", all.size());
        model.addAttribute("activeVendors", all.stream().filter(v -> v.getStatus() == VendorStatus.ACTIVE).count());
        model.addAttribute("highRiskVendors", all.stream().filter(v -> v.riskLevel() == RiskLevel.HIGH).count());
        model.addAttribute("avgRisk", all.stream().mapToDouble(Vendor::getRiskScore).average().orElse(0.0));
        model.addAttribute("riskBins", riskBins(all));
        model.addAttribute("statusCounts", statusCounts(all));
        model.addAttribute("topRisk", all.stream()
            .sorted(Comparator.comparingDouble(Vendor::getRiskScore).reversed()).limit(TOP_RISK_BARS).toList());

        boolean canSeeAlerts = switch (user.getRole()) { case ADMIN, PROCUREMENT_MANAGER, RISK_ANALYST -> true; default -> false; };
        boolean canSeeInventory = switch (user.getRole()) { case ADMIN, PROCUREMENT_MANAGER, WAREHOUSE_MANAGER -> true; default -> false; };
        model.addAttribute("canSeeAlerts", canSeeAlerts);
        model.addAttribute("canSeeInventory", canSeeInventory);
        if (canSeeAlerts) {
            var unresolved = alerts.listUnresolved();
            model.addAttribute("unresolvedAlerts", unresolved.size());
            model.addAttribute("alertFeed", unresolved.stream().limit(FEED_SIZE)
                .map(a -> AlertDto.from(a, id -> names.getOrDefault(id, "Unknown vendor"))).toList());
        }
        if (canSeeInventory) {
            model.addAttribute("lowStock", inventory.lowStockCount());
        }
        return "dashboard";
    }

    /** Histogram over 0-0.2, 0.2-0.4, 0.4-0.6, 0.6-0.8, 0.8+ so the chart can be drawn without an API call. */
    static List<Integer> riskBins(List<Vendor> all) {
        int[] bins = new int[5];
        for (Vendor v : all) {
            double s = v.getRiskScore();
            int idx = s <= 0.2 ? 0 : s <= 0.4 ? 1 : s <= 0.6 ? 2 : s <= 0.8 ? 3 : 4;
            bins[idx]++;
        }
        return Arrays.stream(bins).boxed().toList();
    }

    static Map<String, Long> statusCounts(List<Vendor> all) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (VendorStatus s : List.of(VendorStatus.ACTIVE, VendorStatus.PENDING, VendorStatus.HIGH_RISK, VendorStatus.SUSPENDED, VendorStatus.BLACKLISTED)) {
            counts.put(s.name(), all.stream().filter(v -> v.getStatus() == s).count());
        }
        return counts;
    }
}
