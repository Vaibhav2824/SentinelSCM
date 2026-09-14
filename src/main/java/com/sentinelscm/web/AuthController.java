package com.sentinelscm.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/** Login page. Authentication itself is handled by Spring Security's form login filter. */
@Controller
public class AuthController {

    private static final List<Map<String, String>> DEMO_ACCOUNTS = List.of(
        Map.of("role", "Administrator",       "email", "admin@scm.com",   "password", "admin123"),
        Map.of("role", "Procurement Manager", "email", "pm@scm.com",      "password", "pm123"),
        Map.of("role", "Risk Analyst",        "email", "analyst@scm.com", "password", "analyst123"),
        Map.of("role", "Warehouse Manager",   "email", "wm@scm.com",      "password", "wm123"),
        Map.of("role", "Vendor",              "email", "vendor@scm.com",  "password", "vendor123"));

    private final boolean showDemoCredentials;

    public AuthController(@Value("${sentinel.demo.show-credentials:false}") boolean showDemoCredentials) {
        this.showDemoCredentials = showDemoCredentials;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("showDemoCredentials", showDemoCredentials);
        model.addAttribute("demoAccounts", DEMO_ACCOUNTS);
        return "login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
