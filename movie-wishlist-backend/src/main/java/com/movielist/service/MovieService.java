package com.movielist.service;

import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.exception.ResourceNotFoundException;
import com.movielist.payload.MovieRequest;
import com.movielist.payload.MovieResponse;
import com.movielist.repository.CommentRepository;
import com.movielist.repository.LikeRepository;
import com.movielist.repository.MovieRepository;
import com.movielist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<MovieResponse> getWishlistMovies(User user) {
        List<Movie> movies = movieRepository.findByUserAndStatus(user, Movie.Status.WISHLIST);
        return movies.stream().map(this::convertToMovieResponse).collect(Collectors.toList());
    }

    public List<MovieResponse> getWatchedMovies(User user) {
        List<Movie> movies = movieRepository.findByUserAndStatus(user, Movie.Status.WATCHED);
        return movies.stream().map(this::convertToMovieResponse).collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        return convertToMovieResponse(movie);
    }

    public MovieResponse addMovie(MovieRequest movieRequest, User user) {
        Movie movie = new Movie();
        movie.setTitle(movieRequest.getTitle());
        movie.setGenre(movieRequest.getGenre());
        movie.setReleaseYear(movieRequest.getReleaseYear());
        movie.setRuntime(movieRequest.getRuntime());
        movie.setPosterUrl(movieRequest.getPosterUrl());
        movie.setStatus(Movie.Status.WISHLIST);
        movie.setUser(user);

        Movie savedMovie = movieRepository.save(movie);
        return convertToMovieResponse(savedMovie);
    }

    public MovieResponse updateMovie(Long id, MovieRequest movieRequest, User user) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));

        if (!movie.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to update this movie");
        }

        movie.setTitle(movieRequest.getTitle());
        movie.setGenre(movieRequest.getGenre());
        movie.setReleaseYear(movieRequest.getReleaseYear());
        movie.setRuntime(movieRequest.getRuntime());
        movie.setPosterUrl(movieRequest.getPosterUrl());

        if (movie.getStatus() == Movie.Status.WATCHED) {
            movie.setRating(movieRequest.getRating());
            movie.setReview(movieRequest.getReview());
        }

        Movie updatedMovie = movieRepository.save(movie);
        return convertToMovieResponse(updatedMovie);
    }

    public MovieResponse markAsWatched(Long id, Integer rating, String review, User user) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));

        if (!movie.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to update this movie");
        }

        movie.setStatus(Movie.Status.WATCHED);
        movie.setRating(rating);
        movie.setReview(review);

        Movie updatedMovie = movieRepository.save(movie);
        
        // Check if user earned any badges
        badgeService.checkAndAwardBadges(user);
        
        return convertToMovieResponse(updatedMovie);
    }

    public void deleteMovie(Long id, User user) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));

        if (!movie.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to delete this movie");
        }

        movieRepository.delete(movie);
    }

    public List<MovieResponse> getUserMovies(Long userId, Movie.Status status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Movie> movies;
        if (status != null) {
            movies = movieRepository.findByUserAndStatus(user, status);
        } else {
            movies = movieRepository.findByUser(user);
        }

        return movies.stream().map(this::convertToMovieResponse).collect(Collectors.toList());
    }

    private MovieResponse convertToMovieResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setGenre(movie.getGenre());
        response.setReleaseYear(movie.getReleaseYear());
        response.setRuntime(movie.getRuntime());
        response.setPosterUrl(movie.getPosterUrl());
        response.setStatus(movie.getStatus());
        response.setRating(movie.getRating());
        response.setReview(movie.getReview());
        response.setUserId(movie.getUser().getId());
        response.setUsername(movie.getUser().getUsername());
        
        // Count likes and comments
        response.setLikesCount(likeRepository.countLikesByMovieId(movie.getId()));
        response.setCommentsCount(commentRepository.countCommentsByMovieId(movie.getId()));
        
        // Check if current user liked this movie
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
            if (currentUser != null) {
                response.setUserLiked(likeRepository.existsByUserAndMovie(currentUser, movie));
            }
        }
        
        return response;
    }
    
    @Autowired
    private BadgeService badgeService;
}