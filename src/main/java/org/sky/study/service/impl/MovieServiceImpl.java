package org.sky.study.service.impl;

import org.sky.study.controller.MovieController;
import org.sky.study.dto.TopRatedMovie;
import org.sky.study.exception.ResourceNotFoundException;
import org.sky.study.model.jpa.Movie;
import org.sky.study.repository.jpa.MovieRepository;
import org.sky.study.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private static final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Retrieves all movies with optional filters for title, genre, and release year.
     * @param page the page number
     * @param size the size of the page
     * @param title optional filter for movie title
     * @param genre optional filter for movie genre
     * @param releaseYear optional filter for movie release year
     * @return paginated list of movies with links
     */
    @Override
    public PagedModel<EntityModel<Movie>> getAllMoviesWithFilters(int page, int size, String title, String genre, Integer releaseYear) {

        log.info("Fetching all movies with filters - title: {}, genre: {}, releaseYear: {}, page: {}, size: {}",
                title, genre, releaseYear, page, size);
        if (page < 0 || size <= 0) {
            log.warn("Invalid pagination parameters - page: {}, size: {}", page, size);
            throw new IllegalArgumentException("Page and size parameters must be greater than zero");
        }
        Pageable pageable = PageRequest.of(page, size);
        Specification<Movie> spec = Specification.where(null);

        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (genre != null && !genre.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("genre"), genre));
        }
        if (releaseYear != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("releaseYear"), releaseYear));
        }
        Page<Movie> moviePage = movieRepository.findAll(spec, pageable);
        if (moviePage.isEmpty()) {
            throw new ResourceNotFoundException("No movies found for the given filters");
        }
        List<EntityModel<Movie>> movieModels = moviePage.getContent().stream()
                .map(movie -> EntityModel.of(movie,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class)
                                .getMovieById(movie.getId())).withSelfRel()))
                .collect(Collectors.toList());
        return PagedModel.of(movieModels,
                new PagedModel.PageMetadata(moviePage.getSize(), moviePage.getNumber(),
                        moviePage.getTotalElements(), moviePage.getTotalPages()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class)
                        .getAllMovies(page, size, title, genre, releaseYear)).withSelfRel());
    }

    /**
     * Retrieves top-rated movies.
     * @return list of top-rated movies
     */
    @Override
    public List<TopRatedMovie> getTopRatedMovies() {
        log.info("Fetching top-rated movies");
        List<TopRatedMovie> topRatedMovies = movieRepository.findTopRatedMovies();
        if (topRatedMovies == null || topRatedMovies.isEmpty()) {
            log.info("No top-rated movies found");
            throw new ResourceNotFoundException("No top-rated movies found");
        }
        log.info("Retrieved {} top-rated movies", topRatedMovies.size());
        return topRatedMovies;
    }

    /**
     * Retrieves a movie by its ID.
     * @param movieId the ID of the movie
     * @return the movie if found
     */
    @Override
    public Movie getMovieById(Long movieId) {
        log.info("Fetching movie with ID: {}", movieId);
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with ID " + movieId + " not found"));
    }

    /**
     * Saves or updates a movie.
     * @param movie the movie to save or update
     * @return saved movie
     */
    @Override
    public Movie saveOrUpdateMovie(Movie movie) {
        log.info("Saving or updating movie: {}", movie);
        if (movie == null) {
            log.error("Attempted to save or update a null movie");
            throw new IllegalArgumentException("Movie must not be null");
        }
        return movieRepository.save(movie);
    }

    /**
     * Deletes a movie by id.
     * @param movieId the id of the movie to delete
     */
    @Override
    public void deleteMovie(Long movieId) {
        log.info("Deleting movie with ID: {}", movieId);
        if (movieId == null) {
            throw new IllegalArgumentException("Movie ID must not be null");
        }
        movieRepository.findById(movieId).ifPresentOrElse(
                movie -> movieRepository.deleteById(movieId),
                () -> {
                    throw new ResourceNotFoundException("Attempted to delete non-existent movie with id: " + movieId);
                }
        );
    }
}