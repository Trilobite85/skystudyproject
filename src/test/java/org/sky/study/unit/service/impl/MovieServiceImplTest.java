package org.sky.study.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.sky.study.dto.TopRatedMovie;
import org.sky.study.exception.ResourceNotFoundException;
import org.sky.study.model.jpa.Movie;
import org.sky.study.repository.jpa.MovieRepository;
import org.sky.study.service.impl.MovieServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMovie_success() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi");
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie result = movieService.saveOrUpdateMovie(movie);

        assertEquals(movie, result);
        verify(movieRepository).save(movie);
    }

    @Test
    void addMovie_nullMovie() {
        assertThrows(IllegalArgumentException.class, () -> movieService.saveOrUpdateMovie(null));
    }

    @Test
    void getMovieById_found() {
        Movie movie = new Movie(2L, "Matrix", "Action");
        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(2L);

        assertEquals(movie, result);
    }

    @Test
    void getMovieById_notFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(99L));
    }

    @Test
    void deleteMovie_success() {
        Movie movie = new Movie(3L, "Avatar", "Fantasy");
        when(movieRepository.findById(3L)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(3L);

        verify(movieRepository).deleteById(3L);
    }

    @Test
    void deleteMovie_notFound() {
        when(movieRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(100L));
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void updateMovie_success() {
        Movie movie = new Movie(4L, "Titanic", "Drama");
        when(movieRepository.existsById(4L)).thenReturn(true);
        when(movieRepository.save(movie)).thenReturn(movie);

        Movie result = movieService.saveOrUpdateMovie(movie);

        assertEquals(movie, result);
    }

    @Test
    void getAllMovies_empty() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Movie> moviePage = new PageImpl<>(List.of(), pageable, 0);

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(moviePage);
        assertThrows(ResourceNotFoundException.class,
                () -> movieService.getAllMoviesWithFilters(0, 1, null, null, null));
    }

    @Test
    void getAllMovies_nonEmpty() {
        List<Movie> movies = List.of(
                new Movie(6L, "Jaws", "Thriller"),
                new Movie(7L, "Alien", "Horror")
        );
        Pageable pageable = PageRequest.of(0, 1);
        Page<Movie> moviePage = new PageImpl<>(movies, pageable, movies.size());

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(moviePage);

        PagedModel<EntityModel<Movie>> pagedModelResult = movieService
                .getAllMoviesWithFilters(0, 1, null, null, null);
        List<Movie> moviesResult = pagedModelResult.getContent().stream()
                .map(EntityModel::getContent)
                .toList();

        assertEquals(movies, moviesResult);
    }

    @Test
    void getTopRatedMovies_success() {
        TopRatedMovie topMovie = new TopRatedMovie("Inception", 9.5);
        List<TopRatedMovie> topRatedMovies = List.of(topMovie);

        when(movieRepository.findTopRatedMovies()).thenReturn(topRatedMovies);

        List<TopRatedMovie> result = movieService.getTopRatedMovies();

        assertEquals(topRatedMovies, result);
        verify(movieRepository).findTopRatedMovies();
    }

    @Test
    void getTopRatedMovies_emptyList() {
        when(movieRepository.findTopRatedMovies()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getTopRatedMovies());
        verify(movieRepository).findTopRatedMovies();
    }

    @Test
    void getTopRatedMovies_nullList() {
        when(movieRepository.findTopRatedMovies()).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> movieService.getTopRatedMovies());
        verify(movieRepository).findTopRatedMovies();
    }

}