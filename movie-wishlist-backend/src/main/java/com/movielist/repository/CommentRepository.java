package com.movielist.repository;

import com.movielist.entity.Comment;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMovie(Movie movie);
    List<Comment> findByUser(User user);
    List<Comment> findByMovieOrderByCreatedAtDesc(Movie movie);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.movie.id = :movieId")
    Long countCommentsByMovieId(Long movieId);

    Long countByUser(User user);
}