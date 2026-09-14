package com.sentinelscm.web;

import com.sentinelscm.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Server-rendered pages: they render, they respect roles, and forms round-trip. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WebPagesTest extends AbstractIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    void loginPageRendersWithDemoAccounts() throws Exception {
        mvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("admin@scm.com")))
            .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void rootRedirectsToDashboard() throws Exception {
        mvc.perform(get("/").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("x").roles("VENDOR")))
            .andExpect(redirectedUrl("/dashboard"));
    }

    @ParameterizedTest(name = "{0} sees {1} unlocked and {2} locked")
    @CsvSource({
        "admin@scm.com,   href=\"/inventory\",  nav-item-locked\">",
        "wm@scm.com,      href=\"/inventory\",  href=\"/alerts\"",
        "vendor@scm.com,  href=\"/vendors\",    href=\"/reports\"",
        "analyst@scm.com, href=\"/risk\",       href=\"/inventory\"",
    })
    void sidebarIsDerivedFromRole(String email, String present, String absent) throws Exception {
        mvc.perform(get("/dashboard").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                .user(userDetailsFor(email))))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(present)))
            .andExpect(content().string(not(containsString(absent))));
    }

    @Test
    @WithUserDetails("wm@scm.com")
    void warehouseManagerDashboardHidesAlertsButShowsLowStock() throws Exception {
        mvc.perform(get("/dashboard"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Alert data is not available")))
            .andExpect(content().string(containsString("Low Stock Items")));
    }

    @Test
    @WithUserDetails("wm@scm.com")
    void forbiddenPageRendersErrorTemplate() throws Exception {
        mvc.perform(get("/alerts"))
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({"/vendors", "/risk", "/alerts", "/alerts/1", "/inventory", "/reports", "/vendors/new", "/vendors/2/edit"})
    @WithUserDetails("admin@scm.com")
    void adminCanOpenEveryPage(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("pm@scm.com")
    void vendorFormShowsValidationErrorsAndThenCreates() throws Exception {
        mvc.perform(post("/vendors").with(csrf()).param("name", "").param("contact", "x@y.z").param("rating", "9"))
            .andExpect(status().isOk())
            .andExpect(view().name("vendor-form"))
            .andExpect(content().string(containsString("form-error")));

        mvc.perform(post("/vendors").with(csrf()).param("name", "Formed Co").param("contact", "x@y.z").param("rating", "4.4"))
            .andExpect(redirectedUrl("/vendors"))
            .andExpect(flash().attributeExists("success"));

        mvc.perform(get("/vendors")).andExpect(content().string(containsString("Formed Co")));
    }

    @Test
    @WithUserDetails("analyst@scm.com")
    void riskPageRoundTripsEvaluationAndRecalculation() throws Exception {
        mvc.perform(get("/risk").param("vendorId", "3"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("ReliableGoods Intl")));

        mvc.perform(post("/risk/3/evaluate").with(csrf())
                .param("deliveryTimeliness", "0.1").param("defectRate", "0.7").param("complianceScore", "0.2"))
            .andExpect(redirectedUrl("/risk?vendorId=3"))
            .andExpect(flash().attribute("success", containsString("0.800")));

        mvc.perform(post("/risk/3/calculate").with(csrf()))
            .andExpect(redirectedUrl("/risk?vendorId=3"))
            .andExpect(flash().attribute("error", containsString("HIGH RISK alert")));

        mvc.perform(post("/risk/3/evaluate").with(csrf())
                .param("deliveryTimeliness", "1.5").param("defectRate", "0").param("complianceScore", "1"))
            .andExpect(flash().attribute("error", containsString("between 0 and 1")));
    }

    @Test
    @WithUserDetails("wm@scm.com")
    void inventoryUpdateRoundTrips() throws Exception {
        mvc.perform(post("/inventory/7").with(csrf()).param("quantity", "500"))
            .andExpect(redirectedUrl("/inventory"))
            .andExpect(flash().attribute("success", containsString("500 units")));
        mvc.perform(post("/inventory/7").with(csrf()).param("quantity", "-1"))
            .andExpect(flash().attribute("error", containsString("negative")));
    }

    @Test
    @WithUserDetails("admin@scm.com")
    void adminReportsPageIncludesStrategicTabAndCsvLinks() throws Exception {
        mvc.perform(get("/reports"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Strategic")))
            .andExpect(content().string(containsString("/api/report/strategic.csv")));
    }

    @Test
    @WithUserDetails("pm@scm.com")
    void missingVendorEditPageIs404WithErrorTemplate() throws Exception {
        mvc.perform(get("/vendors/9999/edit"))
            .andExpect(status().isNotFound())
            .andExpect(view().name("error"));
    }

    @Autowired org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private org.springframework.security.core.userdetails.UserDetails userDetailsFor(String email) {
        return userDetailsService.loadUserByUsername(email);
    }
}
