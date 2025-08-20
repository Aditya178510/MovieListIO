package com.movielist.controller;

import com.movielist.exception.ApiException;
import com.movielist.payload.MovieResponse;
import com.movielist.service.TmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private static final Logger logger = LoggerFactory.getLogger(TmdbController.class);

    @Autowired
    private TmdbService tmdbService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam String query,
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> searchResults = tmdbService.searchMovies(query, page);
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            logger.error("Error searching movies: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search movies: " + e.getMessage());
        }
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<MovieResponse> getMovieDetails(@PathVariable Long movieId) {
        try {
            Map<String, Object> movieDetails = tmdbService.getMovieDetails(movieId);
            MovieResponse movieResponse = tmdbService.convertTmdbMovieToMovieResponse(movieDetails);
            return ResponseEntity.ok(movieResponse);
        } catch (Exception e) {
            logger.error("Error getting movie details for ID {}: {}", movieId, e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get movie details: " + e.getMessage());
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularMovies(
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> popularMovies = tmdbService.getPopularMovies(page);
            return ResponseEntity.ok(popularMovies);
        } catch (Exception e) {
            logger.error("Error getting popular movies: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get popular movies: " + e.getMessage());
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedMovies(
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> topRatedMovies = tmdbService.getTopRatedMovies(page);
            return ResponseEntity.ok(topRatedMovies);
        } catch (Exception e) {
            logger.error("Error getting top-rated movies: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get top-rated movies: " + e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingMovies(
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> upcomingMovies = tmdbService.getUpcomingMovies(page);
            return ResponseEntity.ok(upcomingMovies);
        } catch (Exception e) {
            logger.error("Error getting upcoming movies: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get upcoming movies: " + e.getMessage());
        }
    }

    @GetMapping("/movie/{movieId}/recommendations")
    public ResponseEntity<Map<String, Object>> getMovieRecommendations(
            @PathVariable Long movieId,
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> recommendations = tmdbService.getMovieRecommendations(movieId, page);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting movie recommendations for ID {}: {}", movieId, e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get movie recommendations: " + e.getMessage());
        }
    }

    @GetMapping("/search/formatted")
    public ResponseEntity<List<MovieResponse>> searchMoviesFormatted(
            @RequestParam String query,
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> searchResults = tmdbService.searchMovies(query, page);
            List<MovieResponse> formattedResults = new ArrayList<>();
            
            if (searchResults.containsKey("results") && searchResults.get("results") instanceof List) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) searchResults.get("results");
                for (Map<String, Object> movie : results) {
                    formattedResults.add(tmdbService.convertTmdbMovieToMovieResponse(movie));
                }
            }
            
            return ResponseEntity.ok(formattedResults);
        } catch (Exception e) {
            logger.error("Error searching formatted movies: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search formatted movies: " + e.getMessage());
        }
    }
}