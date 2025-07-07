package org.sky.study.integration.controller;

import org.junit.jupiter.api.Test;
import org.sky.study.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MovieControllerIntegrationTest extends SpringBootApplicationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieService movieService;

    @Test
    void getMovies_permitAll() throws Exception {
        mockMvc.perform(get("/movies")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void postMovies_nonAdmin_forbidden() throws Exception {
        mockMvc.perform(post("/movies")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void get_nonexisting_movie_not_found() throws Exception {
        mockMvc.perform(get("/movies/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_movie_with_invalid_input_bad_request() throws Exception {
        String invalidJson = "invalid json";
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_movie_success() throws Exception {
        mockMvc.perform(post("/movies")
                        .content("{\"title\":\"Inception\",\"genre\":\"SyFy\"}")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "USER")
    void get_movie_success() throws Exception {
        mockMvc.perform(get("/movies/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Dark Knight"));
    }

    @Test
    void movies_topRated_permitAll() throws Exception {
        mockMvc.perform(get("/movies/top-rated")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_movies_non_Admin_forbidden() throws Exception {
        mockMvc.perform(delete("/movies/6")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_deletes_none_xisting_movie() throws Exception {
        mockMvc.perform(delete("/movies/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_deletes_movie() throws Exception {
        mockMvc.perform(delete("/movies/9"))
                .andExpect(status().isNoContent());
    }
}