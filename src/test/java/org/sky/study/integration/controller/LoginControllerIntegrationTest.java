package org.sky.study.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginControllerIntegrationTest extends SpringBootApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_success() throws Exception {
        String json = "{\"username\":\"user\",\"password\":\"admin\"}";
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_invalid_credentials() throws Exception {
        String json = "{\"username\":\"user\",\"password\":\"wrong\"}";
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missing_credentials() throws Exception {
        String json = "{wrongJson}";
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    void logout_missing_authorization_header() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Authorization header"));
    }

    @Test
    void logout_malformed_authorization_header() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "InvalidHeader"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Authorization header"));
    }
}