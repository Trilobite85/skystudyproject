package org.sky.study.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.sky.study.exception.ResourceNotFoundException;
import org.sky.study.model.jpa.Movie;
import org.sky.study.model.jpa.Rating;
import org.sky.study.model.jpa.User;
import org.sky.study.repository.jpa.MovieRepository;
import org.sky.study.repository.jpa.RatingRepository;
import org.sky.study.repository.jpa.UserRepository;
import org.sky.study.service.impl.RatingServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrUpdateRating_updatesExistingRating() {
        Rating existing = new Rating();
        existing.setScore(3);
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.of(existing));
        when(ratingRepository.save(existing)).thenReturn(existing);

        Rating result = ratingService.saveOrUpdateRating(1L, 5, "user");

        assertEquals(5, result.getScore());
        verify(ratingRepository).save(existing);
    }

    @Test
    void saveOrUpdateRating_createsNewRating() {
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.empty());
        User user = new User();
        user.setUsername("user");
        Movie movie = new Movie(1L, "Test Movie", "Test Genre");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(inv -> inv.getArgument(0));

        Rating result = ratingService.saveOrUpdateRating(1L, 4, "user");

        assertEquals(4, result.getScore());
        assertEquals(user, result.getUser());
        assertEquals(movie, result.getMovie());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void saveOrUpdateRating_invalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.saveOrUpdateRating(null, 5, "user"));
        assertThrows(IllegalArgumentException.class, () -> ratingService.saveOrUpdateRating(1L, null, "user"));
        assertThrows(IllegalArgumentException.class, () -> ratingService.saveOrUpdateRating(1L, 5, null));
        assertThrows(IllegalArgumentException.class, () -> ratingService.saveOrUpdateRating(1L, 5, "  "));
    }

    @Test
    void saveOrUpdateRating_userNotFound_throwsException() {
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ratingService.saveOrUpdateRating(1L, 5, "user"));
    }

    @Test
    void saveOrUpdateRating_movieNotFound_throwsException() {
        User user = new User();
        user.setUsername("user");
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ratingService.saveOrUpdateRating(1L, 5, "user"));
    }

    @Test
    void deleteUserRating_success() {
        Rating rating = new Rating();
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.of(rating));
        doNothing().when(ratingRepository).delete(rating);

        assertDoesNotThrow(() -> ratingService.deleteUserRating(1L, "user"));
        verify(ratingRepository).delete(rating);
    }

    @Test
    void deleteUserRating_notFound_throwsException() {
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ratingService.deleteUserRating(1L, "user"));
        verify(ratingRepository, never()).delete(any());
    }

    @Test
    void getRatingByMovieIdAndUserName_found() {
        Rating rating = new Rating();
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.of(rating));
        Rating result = ratingService.getRatingByMovieIdAndUserName(1L, "user");
        assertEquals(rating, result);
    }

    @Test
    void getRatingByMovieIdAndUserName_notFound_throwsException() {
        when(ratingRepository.findByMovieIdAndUsername(1L, "user")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ratingService.getRatingByMovieIdAndUserName(1L, "user"));
    }

    @Test
    void getRatingByMovieIdAndUserName_invalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingByMovieIdAndUserName(null, "user"));
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingByMovieIdAndUserName(1L, null));
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingByMovieIdAndUserName(1L, "  "));
    }
}