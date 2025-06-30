package org.sky.study.service.impl;

import org.sky.study.exception.ResourceNotFoundException;
import org.sky.study.model.jpa.Movie;
import org.sky.study.model.jpa.Rating;
import org.sky.study.model.jpa.User;
import org.sky.study.repository.jpa.MovieRepository;
import org.sky.study.repository.jpa.RatingRepository;
import org.sky.study.repository.jpa.UserRepository;
import org.sky.study.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final MovieRepository  movieRepository;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

    public RatingServiceImpl(RatingRepository ratingRepository,
                             MovieRepository movieRepository,
                             UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    /**
     * Saves or updates a movie rating.
     * @param movieId the ID of the movie
     * @param score the rating score
     * @param username the username of the user rating the movie
     * @return the saved or updated rating
     */
    @Override
    public Rating saveOrUpdateRating(Long movieId, Integer score, String username) {
        if (movieId == null || score == null || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        return ratingRepository.findByMovieIdAndUsername(movieId, username)
                .map(existingRating -> updateExistingRating(existingRating, score, username, movieId))
                .orElseGet(() -> createNewRating(movieId, score, username));
    }

    private Rating updateExistingRating(Rating existingRating, Integer score, String username, Long movieId) {
        existingRating.setScore(score);
        log.info("Updating existing rating for user {} and movie ID {}", username, movieId);
        return ratingRepository.save(existingRating);
    }

    private Rating createNewRating(Long movieId, Integer score, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

        Rating newRating = new Rating();
        newRating.setMovie(movie);
        newRating.setScore(score);
        newRating.setUser(user);
        log.info("Creating new rating for user {} and movie {}", user.getUsername(), movie.getTitle());
        return ratingRepository.save(newRating);
    }

    /**
     * Deletes a movie rating by movie ID.
     * @param movieId the ID of the movie
     * @param username the Name of the user who rated the movie
     */
    @Override
    public void deleteUserRating(Long movieId, String username) {
        log.info("Deleting rating for user {} and movie ID {}", username, movieId);
        Rating rating = ratingRepository.findByMovieIdAndUsername(movieId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for movie ID: " + movieId + " and username: " + username));
        ratingRepository.delete(rating);
    }

    /**
     * Retrieves a rating by movie ID and username.
     * @param movieId the ID of the movie
     * @param username the username of the user
     * @return an Optional containing the rating if found, otherwise empty
     */
    @Override
    public Rating getRatingByMovieIdAndUserName(Long movieId, String username) {
        log.info("Fetching ratings for movie ID {} by user {}", movieId, username);
        if (movieId == null || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Invalid input parameters: movieId and username must not be null or blank");
        }
        return ratingRepository.findByMovieIdAndUsername(movieId, username).orElseThrow(() -> {;
            log.warn("Rating not found for movie ID {} and username {}", movieId, username);
            return new ResourceNotFoundException("Rating not found for movie ID " + movieId + " and username " + username);
        });
    }
}