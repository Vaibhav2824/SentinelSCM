package com.sentinelscm.api;

import com.sentinelscm.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Authentication behaviour and the role matrix, exercised over HTTP. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiSecurityTest extends AbstractIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    void anonymousApiCallGets401NotARedirect() throws Exception {
        mvc.perform(get("/api/vendor"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousPageGetsRedirectedToLogin() throws Exception {
        mvc.perform(get("/dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void healthEndpointsArePublic() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.strategy").value("weighted"));
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void formLoginWithSeededCredentialsSucceeds() throws Exception {
        mvc.perform(formLogin("/login").user("admin@scm.com").password("admin123"))
            .andExpect(authenticated().withRoles("ADMIN"))
            .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void formLoginWithWrongPasswordFails() throws Exception {
        mvc.perform(formLogin("/login").user("admin@scm.com").password("nope"))
            .andExpect(unauthenticated())
            .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithUserDetails("vendor@scm.com")
    void logoutInvalidatesSession() throws Exception {
        mvc.perform(post("/logout").with(csrf()))
            .andExpect(unauthenticated())
            .andExpect(redirectedUrl("/login?logout"));
    }

    @ParameterizedTest(name = "{0} {1} as {2} -> {3}")
    @CsvSource({
        // vendors
        "GET,    /api/vendor,              VENDOR,              200",
        "GET,    /api/vendor/1,            WAREHOUSE_MANAGER,   403",
        "DELETE, /api/vendor/1,            PROCUREMENT_MANAGER, 403",
        "DELETE, /api/vendor/1,            ADMIN,               200",
        "PUT,    /api/vendor/1/suspend,    PROCUREMENT_MANAGER, 200",
        "PUT,    /api/vendor/1/blacklist,  PROCUREMENT_MANAGER, 403",
        // risk
        "POST,   /api/risk/calculate/1,    PROCUREMENT_MANAGER, 403",
        "POST,   /api/risk/calculate/1,    RISK_ANALYST,        200",
        "GET,    /api/vendor/1/risk,       VENDOR,              200",
        // alerts
        "GET,    /api/alert,               WAREHOUSE_MANAGER,   403",
        "GET,    /api/alert,               RISK_ANALYST,        200",
        "PUT,    /api/alert/1/resolve,     VENDOR,              403",
        // inventory
        "GET,    /api/inventory,           RISK_ANALYST,        403",
        "GET,    /api/inventory,           PROCUREMENT_MANAGER, 200",
        // reports
        "GET,    /api/report/performance,  WAREHOUSE_MANAGER,   403",
        "GET,    /api/report/strategic,    PROCUREMENT_MANAGER, 403",
        "GET,    /api/report/strategic,    ADMIN,               200",
        "GET,    /api/report/strategic.csv,ADMIN,               200",
    })
    void roleMatrix(String method, String path, String role, int expected) throws Exception {
        var request = switch (method) {
            case "GET" -> get(path);
            case "POST" -> post(path).with(csrf());
            case "PUT" -> put(path).with(csrf());
            case "DELETE" -> delete(path).with(csrf());
            default -> throw new IllegalArgumentException(method);
        };
        mvc.perform(request.with(user("matrix@scm.com").roles(role)))
            .andExpect(status().is(expected));
    }

    @Test
    @WithUserDetails("wm@scm.com")
    void forbiddenApiCallReturnsJsonEnvelope() throws Exception {
        mvc.perform(get("/api/alert"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.path").value("/api/alert"))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
