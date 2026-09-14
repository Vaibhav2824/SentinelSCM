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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VendorApiTest extends AbstractIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    @WithUserDetails("pm@scm.com")
    void listsSeededVendorsWithDerivedRiskLevel() throws Exception {
        mvc.perform(get("/api/vendor"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(12)))
            .andExpect(jsonPath("$[1].name").value("FastSupply Co"))
            .andExpect(jsonPath("$[1].riskLevel").value("HIGH"))
            .andExpect(jsonPath("$[1].status").value("HIGH_RISK"));
    }

    @Test
    @WithUserDetails("pm@scm.com")
    void createsUpdatesAndSoftDeletesAVendor() throws Exception {
        String location = mvc.perform(post("/api/vendor").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"  NewCo  \",\"contact\":\"hi@newco.io\",\"rating\":4.2}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("NewCo"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.riskLevel").value("LOW"))
            .andReturn().getResponse().getContentAsString();
        int id = Integer.parseInt(location.replaceAll(".*\"vendorId\":(\\d+).*", "$1"));

        mvc.perform(put("/api/vendor/" + id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NewCo Ltd\",\"contact\":\"hi@newco.io\",\"rating\":3.9}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("NewCo Ltd"))
            .andExpect(jsonPath("$.rating").value(3.9));

        mvc.perform(put("/api/vendor/" + id + "/suspend").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUSPENDED"));

        mvc.perform(get("/api/vendor")).andExpect(jsonPath("$", hasSize(13)));
    }

    @Test
    @WithUserDetails("admin@scm.com")
    void softDeletedVendorDisappearsFromListButStaysFetchable() throws Exception {
        mvc.perform(delete("/api/vendor/12").with(csrf())).andExpect(status().isOk());
        mvc.perform(get("/api/vendor")).andExpect(jsonPath("$", hasSize(11)));
        mvc.perform(get("/api/vendor/12")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @WithUserDetails("pm@scm.com")
    void rejectsInvalidPayloadWithFieldErrors() throws Exception {
        mvc.perform(post("/api/vendor").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"rating\":7}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors.name").exists())
            .andExpect(jsonPath("$.fieldErrors.rating").exists());
    }

    @Test
    @WithUserDetails("pm@scm.com")
    void unknownVendorIs404() throws Exception {
        mvc.perform(get("/api/vendor/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Vendor 9999 not found"));
    }
}
