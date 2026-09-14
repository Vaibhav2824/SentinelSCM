package com.sentinelscm.web;

import com.sentinelscm.domain.Vendor;
import com.sentinelscm.security.Access;
import com.sentinelscm.security.SecurityUser;
import com.sentinelscm.service.RiskResult;
import com.sentinelscm.service.RiskService;
import com.sentinelscm.service.VendorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/risk")
public class RiskController {

    private final RiskService risk;
    private final VendorService vendors;

    public RiskController(RiskService risk, VendorService vendors) {
        this.risk = risk;
        this.vendors = vendors;
    }

    @GetMapping
    @PreAuthorize(Access.ADMIN_PM_RA)
    public String page(@RequestParam(required = false) Integer vendorId, Model model) {
        List<Vendor> all = vendors.listVisible();
        Vendor selected = vendorId != null ? vendors.get(vendorId) : all.stream().findFirst().orElse(null);
        model.addAttribute("vendors", all);
        model.addAttribute("selected", selected);
        model.addAttribute("config", risk.config());
        if (selected != null) {
            model.addAttribute("latest", risk.latestCriteria(selected.getId()).orElse(null));
            model.addAttribute("history", risk.history(selected.getId()));
        }
        return "risk";
    }

    @PostMapping("/{id}/evaluate")
    @PreAuthorize(Access.ADMIN_PM_RA)
    public String evaluate(@PathVariable Integer id,
                           @RequestParam double deliveryTimeliness,
                           @RequestParam double defectRate,
                           @RequestParam double complianceScore,
                           RedirectAttributes flash) {
        if (outOfRange(deliveryTimeliness) || outOfRange(defectRate) || outOfRange(complianceScore)) {
            flash.addFlashAttribute("error", "Each evaluation value must be between 0 and 1.");
            return "redirect:/risk?vendorId=" + id;
        }
        double preview = risk.submitEvaluation(id, deliveryTimeliness, defectRate, complianceScore);
        flash.addFlashAttribute("success", String.format("Evaluation saved. Projected risk score: %.3f. Click Recalculate to apply it.", preview));
        return "redirect:/risk?vendorId=" + id;
    }

    @PostMapping("/{id}/calculate")
    @PreAuthorize(Access.ADMIN_RA)
    public String calculate(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            RiskResult r = risk.calculateRisk(id);
            if (r.alertTriggered()) {
                flash.addFlashAttribute("error", String.format(
                    "Risk score %.3f exceeds threshold %.2f. HIGH RISK alert #%d raised with recommendations.", r.score(), r.threshold(), r.alertId()));
            } else {
                flash.addFlashAttribute("success", String.format("Risk score %.3f recorded (threshold %.2f). No alert.", r.score(), r.threshold()));
            }
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/risk?vendorId=" + id;
    }

    @PostMapping("/threshold")
    @PreAuthorize(Access.ADMIN)
    public String threshold(@RequestParam double threshold, @AuthenticationPrincipal SecurityUser user,
                            @RequestParam(required = false) Integer vendorId, RedirectAttributes flash) {
        try {
            risk.updateThreshold(threshold, user.getId());
            flash.addFlashAttribute("success", String.format("Alert threshold set to %.2f.", threshold));
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/risk" + (vendorId != null ? "?vendorId=" + vendorId : "");
    }

    private static boolean outOfRange(double v) { return v < 0 || v > 1; }
}
