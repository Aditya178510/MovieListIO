package com.movielist.repository;

import com.movielist.entity.Like;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByMovie(Movie movie);
    List<Like> findByUser(User user);
    Optional<Like> findByUserAndMovie(User user, Movie movie);
    boolean existsByUserAndMovie(User user, Movie movie);
    void deleteByUserAndMovie(User user, Movie movie);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.movie.id = :movieId")
    Long countLikesByMovieId(Long movieId);

    Long countByUser(User user);
}