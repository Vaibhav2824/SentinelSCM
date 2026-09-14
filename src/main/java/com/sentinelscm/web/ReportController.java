package com.sentinelscm.web;

import com.sentinelscm.security.Access;
import com.sentinelscm.security.SecurityUser;
import com.sentinelscm.domain.Role;
import com.sentinelscm.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/reports")
    @PreAuthorize(Access.ADMIN_PM_RA)
    public String reports(@AuthenticationPrincipal SecurityUser user, Model model) {
        model.addAttribute("performance", reports.performance());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("strategic", isAdmin ? reports.strategic() : null);
        return "reports";
    }
}
