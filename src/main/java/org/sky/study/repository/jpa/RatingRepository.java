package org.sky.study.repository.jpa;

import org.sky.study.model.jpa.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Finds a rating by movie ID and username.
     * @param movieId the ID of the movie
     * @param username the username of the user
     * @return the rating if found, otherwise null
     */
    @Query("SELECT r FROM Rating r WHERE r.movie.id = :movieId AND r.user.username = :username")
    Optional<Rating> findByMovieIdAndUsername(Long movieId, String username);
}
