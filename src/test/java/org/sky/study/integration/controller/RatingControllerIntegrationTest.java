package org.sky.study.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RatingControllerIntegrationTest extends SpringBootApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymous_user_cannot_rate_movie() throws Exception {
        mockMvc.perform(post("/movies/1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void rate_nonexisting_movie_not_found() throws Exception {
        mockMvc.perform(post("/movies/999/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void rate_movie_with_invalid_input_bad_request() throws Exception {
        mockMvc.perform(post("/movies/2/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void rate_movie_success() throws Exception {
        mockMvc.perform(post("/movies/2/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(5));
    }

    @Test
    @WithMockUser(username = "admin")
    void get_ratings_for_movie_success() throws Exception {
        mockMvc.perform(get("/movies/3/ratings"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    void delete_ratings_for_movie_success() throws Exception {
        mockMvc.perform(delete("/movies/2/ratings"))
                .andExpect(status().isNoContent());
    }
}