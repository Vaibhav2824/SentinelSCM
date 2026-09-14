package com.sentinelscm.api;

import com.sentinelscm.api.dto.AlertDto;
import com.sentinelscm.domain.Recommendation;
import com.sentinelscm.domain.Vendor;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.AlertService;
import com.sentinelscm.service.RecommendationService;
import com.sentinelscm.service.VendorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alert")
@PreAuthorize(Access.ADMIN_PM_RA)
public class AlertApiController {

    private final AlertService alerts;
    private final RecommendationService recommendations;
    private final VendorService vendors;

    public AlertApiController(AlertService alerts, RecommendationService recommendations, VendorService vendors) {
        this.alerts = alerts;
        this.recommendations = recommendations;
        this.vendors = vendors;
    }

    @GetMapping
    public List<AlertDto> list(@RequestParam(defaultValue = "false") boolean unresolvedOnly) {
        Function<Integer, String> names = vendorNames();
        var source = unresolvedOnly ? alerts.listUnresolved() : alerts.listAll();
        return source.stream().map(a -> AlertDto.from(a, names)).toList();
    }

    @PutMapping("/{id}/resolve")
    public AlertDto resolve(@PathVariable Integer id) {
        return AlertDto.from(alerts.resolve(id), vendorNames());
    }

    @GetMapping("/{id}/recommendation")
    public List<Map<String, Object>> recommendations(@PathVariable Integer id) {
        alerts.get(id);
        Function<Integer, String> names = vendorNames();
        return recommendations.forAlert(id).stream()
            .map(r -> Map.<String, Object>of(
                "recId", r.getId(),
                "suggestedVendorId", r.getSuggestedVendorId(),
                "suggestedVendorName", names.apply(r.getSuggestedVendorId()),
                "reason", r.getReason()))
            .toList();
    }

    private Function<Integer, String> vendorNames() {
        Map<Integer, String> byId = vendors.listVisible().stream()
            .collect(Collectors.toMap(Vendor::getId, Vendor::getName));
        return id -> byId.getOrDefault(id, "Unknown vendor");
    }
}
