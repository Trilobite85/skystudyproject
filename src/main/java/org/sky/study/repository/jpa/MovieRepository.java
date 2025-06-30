package org.sky.study.repository.jpa;

import org.sky.study.dto.TopRatedMovie;
import org.sky.study.model.jpa.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {


    /**
     * Retrieves top-rated movies with their average rating.
     * @return list of top-rated movies
     */
    @Query("SELECT new org.sky.study.dto.TopRatedMovie(m.title, AVG(r.score)) " +
        "FROM Movie m JOIN m.ratings r GROUP BY m.id, m.title " +
        "ORDER BY AVG(r.score) DESC")
    List<TopRatedMovie> findTopRatedMovies();
}
