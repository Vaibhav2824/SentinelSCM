package com.sentinelscm.web;

import com.sentinelscm.api.dto.AlertDto;
import com.sentinelscm.domain.Vendor;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.AlertService;
import com.sentinelscm.service.RecommendationService;
import com.sentinelscm.service.VendorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/alerts")
@PreAuthorize(Access.ADMIN_PM_RA)
public class AlertController {

    private final AlertService alerts;
    private final RecommendationService recommendations;
    private final VendorService vendors;

    public AlertController(AlertService alerts, RecommendationService recommendations, VendorService vendors) {
        this.alerts = alerts;
        this.recommendations = recommendations;
        this.vendors = vendors;
    }

    @GetMapping
    public String list(Model model) {
        Function<Integer, String> names = vendorNames();
        model.addAttribute("alerts", alerts.listAll().stream().map(a -> AlertDto.from(a, names)).toList());
        model.addAttribute("unresolvedCount", alerts.unresolvedCount());
        return "alerts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Function<Integer, String> names = vendorNames();
        model.addAttribute("alert", AlertDto.from(alerts.get(id), names));
        model.addAttribute("recommendations", recommendations.forAlert(id).stream()
            .map(r -> Map.of("vendorId", r.getSuggestedVendorId(), "vendorName", names.apply(r.getSuggestedVendorId()), "reason", r.getReason()))
            .toList());
        return "alert-detail";
    }

    @PostMapping("/{id}/resolve")
    public String resolve(@PathVariable Integer id, RedirectAttributes flash) {
        alerts.resolve(id);
        flash.addFlashAttribute("success", "Alert #" + id + " resolved.");
        return "redirect:/alerts";
    }

    private Function<Integer, String> vendorNames() {
        Map<Integer, String> byId = vendors.listVisible().stream().collect(Collectors.toMap(Vendor::getId, Vendor::getName));
        return id -> byId.getOrDefault(id, "Unknown vendor");
    }
}
