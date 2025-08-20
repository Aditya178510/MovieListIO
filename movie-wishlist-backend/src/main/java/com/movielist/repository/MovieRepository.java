package com.movielist.repository;

import com.movielist.entity.Movie;
import com.movielist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByUserAndStatus(User user, Movie.Status status);
    List<Movie> findByUser(User user);
    
    @Query("SELECT m FROM Movie m WHERE m.user.id = :userId AND m.status = 'WATCHED'")
    List<Movie> findWatchedMoviesByUserId(Long userId);
    
    @Query("SELECT m FROM Movie m WHERE m.user.id = :userId AND m.status = 'WISHLIST'")
    List<Movie> findWishlistMoviesByUserId(Long userId);
    
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.user.id = :userId AND m.status = 'WATCHED'")
    Long countWatchedMoviesByUserId(Long userId);
    
    @Query("SELECT COALESCE(SUM(m.runtime),0) FROM Movie m WHERE m.user.id = :userId AND m.status = 'WATCHED'")
    Long calculateTotalWatchTimeByUserId(Long userId);
}