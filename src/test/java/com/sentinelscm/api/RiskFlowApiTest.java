package com.sentinelscm.api;

import com.sentinelscm.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The headline scenario: evaluation -> Strategy -> threshold -> Factory alert ->
 * Observer listeners persist recommendations, all visible through the API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RiskFlowApiTest extends AbstractIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    @WithUserDetails("analyst@scm.com")
    void badEvaluationRaisesAlertWithRecommendationsAndFlipsVendorStatus() throws Exception {
        // ReliableGoods (id 3) is the safest vendor; feed it a terrible evaluation.
        mvc.perform(post("/api/vendor/3/evaluate").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deliveryTimeliness\":0.2,\"defectRate\":0.6,\"complianceScore\":0.3}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.previewScore").value(0.7))   // (0.8*0.4)+(0.6*0.4)+(0.7*0.2)=0.70
            .andExpect(jsonPath("$.strategy").value("weighted"));

        // 0.70 is not > 0.70: no alert yet
        mvc.perform(post("/api/risk/calculate/3").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alertTriggered").value(false))
            .andExpect(jsonPath("$.threshold").value(0.7));

        mvc.perform(post("/api/vendor/3/evaluate").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deliveryTimeliness\":0.1,\"defectRate\":0.7,\"complianceScore\":0.2}"))
            .andExpect(status().isOk());

        String body = mvc.perform(post("/api/risk/calculate/3").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alertTriggered").value(true))
            .andExpect(jsonPath("$.score").value(0.8))
            .andExpect(jsonPath("$.alertId").isNumber())
            .andReturn().getResponse().getContentAsString();
        int alertId = Integer.parseInt(body.replaceAll(".*\"alertId\":(\\d+).*", "$1"));

        mvc.perform(get("/api/vendor/3"))
            .andExpect(jsonPath("$.status").value("HIGH_RISK"))
            .andExpect(jsonPath("$.riskScore").value(0.8));

        mvc.perform(get("/api/vendor/3/risk"))
            .andExpect(jsonPath("$", hasSize(6)))              // 4 seeded + 2 new, newest first
            .andExpect(jsonPath("$[0].score").value(0.8));

        // Observer side effect: up to 3 low-risk ACTIVE alternatives, best first, never vendor 3 itself
        mvc.perform(get("/api/alert/" + alertId + "/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].suggestedVendorName").value("Prime Materials Co"))
            .andExpect(jsonPath("$[*].suggestedVendorId").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem(3))));

        mvc.perform(get("/api/alert?unresolvedOnly=true"))
            .andExpect(jsonPath("$[0].alertId").value(alertId))
            .andExpect(jsonPath("$[0].vendorName").value("ReliableGoods Intl"))
            .andExpect(jsonPath("$[0].severity").value("HIGH"));

        mvc.perform(put("/api/alert/" + alertId + "/resolve").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resolved").value(true));
    }

    @Test
    @WithUserDetails("analyst@scm.com")
    void analystCannotCreateVendors() throws Exception {
        mvc.perform(post("/api/vendor").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Fresh Vendor\",\"rating\":3}"))
            .andExpect(status().isForbidden()); // analysts cannot create vendors
    }

    @Test
    @WithUserDetails("admin@scm.com")
    void adminCanLowerThresholdWhichChangesTheOutcome() throws Exception {
        mvc.perform(put("/api/risk/threshold").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"threshold\":0.1}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.threshold").value(0.1));

        // QuickParts (id 1) latest criteria scores 0.07: still below 0.1
        mvc.perform(post("/api/risk/calculate/1").with(csrf()))
            .andExpect(jsonPath("$.alertTriggered").value(false));

        // MetalWorks (id 6) latest criteria: (0.12*0.4)+(0.07*0.4)+(0.10*0.2)=0.096 -> still no; NexusBuild (11): 0.1+0.06+0.04=0.2 -> alert
        mvc.perform(post("/api/risk/calculate/11").with(csrf()))
            .andExpect(jsonPath("$.alertTriggered").value(true))
            .andExpect(jsonPath("$.score").value(0.2));

        mvc.perform(put("/api/risk/threshold").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"threshold\":1.5}"))
            .andExpect(status().isBadRequest());
    }
}
