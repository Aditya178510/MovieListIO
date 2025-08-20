package com.movielist.service;

import com.movielist.entity.Comment;
import com.movielist.entity.Like;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.exception.ResourceNotFoundException;
import com.movielist.payload.CommentRequest;
import com.movielist.payload.CommentResponse;
import com.movielist.repository.CommentRepository;
import com.movielist.repository.LikeRepository;
import com.movielist.repository.MovieRepository;
import com.movielist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.LocalDateTime;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    public void likeMovie(Long movieId, User user) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));
        
        if (likeRepository.existsByUserAndMovie(user, movie)) {
            throw new RuntimeException("You have already liked this movie");
        }
        
        Like like = new Like();
        like.setUser(user);
        like.setMovie(movie);
        
        likeRepository.save(like);
    }

    public void unlikeMovie(Long movieId, User user) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));
        
        Like like = likeRepository.findByUserAndMovie(user, movie)
                .orElseThrow(() -> new RuntimeException("You have not liked this movie"));
        
        likeRepository.delete(like);
    }

    public CommentResponse addComment(Long movieId, CommentRequest commentRequest, User user) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));
        
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setMovie(movie);
        
        Comment savedComment = commentRepository.save(comment);
        
        return convertToCommentResponse(savedComment);
    }

    public List<CommentResponse> getMovieComments(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));
        
        List<Comment> comments = commentRepository.findByMovieOrderByCreatedAtDesc(movie);
        
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        
        // Check if the user is the owner of the comment or an admin
        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }
        
        commentRepository.delete(comment);
    }

    public List<Long> getUserLikedMovies(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        List<Like> likes = likeRepository.findByUser(user);
        
        return likes.stream()
                .map(like -> like.getMovie().getId())
                .collect(Collectors.toList());
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt()
        .atZone(ZoneId.systemDefault())
        .toInstant());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setUserProfilePictureUrl(comment.getUser().getProfilePictureUrl());
        response.setMovieId(comment.getMovie().getId());
        response.setMovieTitle(comment.getMovie().getTitle());
        
        return response;
    }
}