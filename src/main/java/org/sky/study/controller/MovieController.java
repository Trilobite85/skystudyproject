package org.sky.study.controller;

import org.sky.study.dto.TopRatedMovie;
import org.sky.study.model.jpa.Movie;
import org.sky.study.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private static final Logger log = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Retrieves all movies with optional filters for title, genre, and release year.
     * @param page the page number to retrieve
     * @param size the number of movies per page
     * @param title optional filter for movie title
     * @param genre optional filter for movie genre
     * @param releaseYear optional filter for movie release year
     * @return paginated list of movies
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Movie>>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer releaseYear) {
        PagedModel<EntityModel<Movie>> pagedMovies = movieService.getAllMoviesWithFilters(page, size, title, genre, releaseYear);
        return ResponseEntity.ok(pagedMovies);
    }

    /**
     * Retrieves a movie by its ID.
     * @param movieId the ID of the movie to retrieve
     * @return the movie if found, or 404 Not Found if not found
     */
    @GetMapping("/{movieId}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long movieId) {
        Movie movie = movieService.getMovieById(movieId);
        return ResponseEntity.ok(movie);
    }

    /**
     * Retrieves a list of top-rated movies.
     * @return list of top-rated movies
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<TopRatedMovie>> getTopRatedMovies() {
        List<TopRatedMovie> topRatedMovies = movieService.getTopRatedMovies();
        return ResponseEntity.ok(topRatedMovies);
    }

    /**
     * Saves or updates a movie.
     * @param movie the movie to save or update
     * @return the saved or updated movie
     */
    @PostMapping
    public ResponseEntity<Movie> saveOrUpdateMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.saveOrUpdateMovie(movie);
        log.info("Movie saved successfully with ID: {}", savedMovie.getId());
        return ResponseEntity.ok(savedMovie);
    }

    /**
     * Deletes a movie by its ID.
     * @param movieId the ID of the movie to delete
     * @return response indicating success
     */
    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        log.info("Successfully deleted movie with ID: {}", movieId);
        return ResponseEntity.noContent().build();
    }
}