package org.sky.study.controller;

import org.sky.study.dto.RatingRequest;
import org.sky.study.model.jpa.Rating;
import org.sky.study.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies/{movieId}/ratings")
public class RatingController {

    private final RatingService ratingService;
    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }
    /**
     * Retrieves ratings for a specific movie by its ID.
     * @param movieId the ID of the movie
     * @return the rating if found
     */
    @GetMapping
    public ResponseEntity<Rating> getUserRatingByMovieId(@PathVariable Long movieId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ratingService.getRatingByMovieIdAndUserName(movieId, username));
    }

    @PostMapping
    public ResponseEntity<Rating> saveOrUpdateRating(
            @PathVariable Long movieId,
            @RequestBody RatingRequest score) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} is saving or updating rating for movie ID {} with score {}", username, movieId, score.getScore());
        Rating savedRating = ratingService.saveOrUpdateRating(movieId, score.getScore(), username);
        return ResponseEntity.ok(savedRating);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long movieId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ratingService.deleteUserRating(movieId, username);
        return ResponseEntity.noContent().build();
    }

}