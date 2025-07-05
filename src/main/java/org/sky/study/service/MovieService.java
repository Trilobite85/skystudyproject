package org.sky.study.service;

import org.sky.study.dto.TopRatedMovie;
import org.sky.study.model.jpa.Movie;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    PagedModel<EntityModel<Movie>> getAllMoviesWithFilters(int page, int size, String title, String genre, Integer releaseYear);
    Movie getMovieById(Long movieId);
    List<TopRatedMovie> getTopRatedMovies();
    Movie saveOrUpdateMovie(Movie movie);
    void deleteMovie(Long movieId);
}
