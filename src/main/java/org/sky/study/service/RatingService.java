package org.sky.study.service;

import org.sky.study.model.jpa.Rating;

import java.util.Optional;

public interface RatingService {

    Optional<Rating> getRatingByMovieIdAndUserName(Long movieId, String username);
    Rating saveOrUpdateRating(Long movieId, Integer score, String username);
    void deleteUserRating(Long movieId, String username);

}