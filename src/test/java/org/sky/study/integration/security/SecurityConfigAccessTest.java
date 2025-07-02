package org.sky.study.integration.security;

import org.junit.jupiter.api.Test;
import org.sky.study.controller.RatingController;
import org.sky.study.model.jpa.Movie;
import org.sky.study.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private RatingController ratingController;


    @Test
    void moviesTopRated_permitAll() throws Exception {
        mockMvc.perform(get("/movies/top-rated")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void moviesRatings_authenticated() throws Exception {
        mockMvc.perform(get("/movies/1/ratings")).andExpect(status().isOk());
    }

    @Test
    void moviesRatings_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(get("/movies/1/ratings")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postMovies_adminOnly() throws Exception {
        Movie movie = new Movie(1L, "Inception", "2010");
        when(movieService.saveOrUpdateMovie(any())).thenReturn(new Movie(1L, "Inception", "SiFy"));
        mockMvc.perform(post("/movies")
                        .content("{\"title\":\"Inception\",\"genre\":\"SyFy\"}")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void postMovies_nonAdmin_forbidden() throws Exception {
        mockMvc.perform(post("/movies")).andExpect(status().isForbidden());
    }

    @Test
    void getMovies_permitAll() throws Exception {
        mockMvc.perform(get("/movies")).andExpect(status().isOk());
    }

    @Test
    void getMovieById_permitAll() throws Exception {
        mockMvc.perform(get("/movies/1")).andExpect(status().isOk());
    }
}