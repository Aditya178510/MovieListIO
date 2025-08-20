package com.movielist.service;

import com.movielist.payload.MovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TmdbService {

    private static final Logger logger = LoggerFactory.getLogger(TmdbService.class);

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> searchMovies(String query, Integer page) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", query)
                .queryParam("page", page != null ? page : 1)
                .build()
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("TMDB API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error searching movies: " + e.getMessage(), e);
        } catch (RestClientException e) {
            logger.error("TMDB API connection error", e);
            throw new RuntimeException("Error connecting to movie database: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getMovieDetails(Long movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/" + movieId)
                .queryParam("api_key", apiKey)
                .build()
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("TMDB API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error getting movie details: " + e.getMessage(), e);
        } catch (RestClientException e) {
            logger.error("TMDB API connection error", e);
            throw new RuntimeException("Error connecting to movie database: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getPopularMovies(Integer page) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/popular")
                .queryParam("api_key", apiKey)
                .queryParam("page", page != null ? page : 1)
                .build()
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("TMDB API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error getting popular movies: " + e.getMessage(), e);
        } catch (RestClientException e) {
            logger.error("TMDB API connection error", e);
            throw new RuntimeException("Error connecting to movie database: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getTopRatedMovies(Integer page) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/top_rated")
                .queryParam("api_key", apiKey)
                .queryParam("page", page != null ? page : 1)
                .build()
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

    public Map<String, Object> getUpcomingMovies(Integer page) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/upcoming")
                .queryParam("api_key", apiKey)
                .queryParam("page", page != null ? page : 1)
                .build()
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

    public Map<String, Object> getMovieRecommendations(Long movieId, Integer page) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/" + movieId + "/recommendations")
                .queryParam("api_key", apiKey)
                .queryParam("page", page != null ? page : 1)
                .build()
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("TMDB API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error getting movie recommendations: " + e.getMessage(), e);
        } catch (RestClientException e) {
            logger.error("TMDB API connection error", e);
            throw new RuntimeException("Error connecting to movie database: " + e.getMessage(), e);
        }
    }

    // Helper method to convert TMDB movie data to our MovieResponse format
    public MovieResponse convertTmdbMovieToMovieResponse(Map<String, Object> tmdbMovie) {
        try {
            MovieResponse movieResponse = new MovieResponse();
            
            // TMDB uses 'id' for movie ID
            if (tmdbMovie.containsKey("id")) {
                movieResponse.setId(Long.valueOf(tmdbMovie.get("id").toString()));
            }
            
            // TMDB uses 'title' for movie title
            if (tmdbMovie.containsKey("title")) {
                movieResponse.setTitle((String) tmdbMovie.get("title"));
            }
            
            // TMDB uses 'genre_ids' or has nested 'genres' object
            // For simplicity, we'll just use the first genre if available
            if (tmdbMovie.containsKey("genres") && tmdbMovie.get("genres") instanceof List) {
                List<Map<String, Object>> genres = (List<Map<String, Object>>) tmdbMovie.get("genres");
                if (!genres.isEmpty()) {
                    movieResponse.setGenre((String) genres.get(0).get("name"));
                }
            }
            
            // TMDB uses 'release_date' in format 'YYYY-MM-DD'
            if (tmdbMovie.containsKey("release_date") && tmdbMovie.get("release_date") != null) {
                String releaseDate = (String) tmdbMovie.get("release_date");
                if (releaseDate.length() >= 4) {
                    movieResponse.setReleaseYear(Integer.valueOf(releaseDate.substring(0, 4)));
                }
            }
            
            // TMDB uses 'runtime' for movie duration in minutes
            if (tmdbMovie.containsKey("runtime") && tmdbMovie.get("runtime") != null) {
                movieResponse.setRuntime(Integer.valueOf(tmdbMovie.get("runtime").toString()));
            }
            
            // TMDB uses 'poster_path' for poster URL, need to prepend base image URL
            if (tmdbMovie.containsKey("poster_path") && tmdbMovie.get("poster_path") != null) {
                movieResponse.setPosterUrl("https://image.tmdb.org/t/p/w500" + tmdbMovie.get("poster_path"));
            }
            
            // Set default values for our custom fields
            movieResponse.setStatus(null); // External movies don't have a status yet
            movieResponse.setRating(null); // External movies don't have user ratings yet
            movieResponse.setReview(null); // External movies don't have user reviews yet
            movieResponse.setUserId(null); // External movies don't belong to a user yet
            movieResponse.setUsername(null); // External movies don't belong to a user yet
            movieResponse.setLikesCount(0L); // External movies don't have likes yet
            movieResponse.setCommentsCount(0L); // External movies don't have comments yet
            movieResponse.setUserLiked(false); // Current user hasn't liked this external movie yet
            
            return movieResponse;
        } catch (ClassCastException | NullPointerException | NumberFormatException e) {
            logger.error("Error converting TMDB movie data: {}", e.getMessage());
            throw new RuntimeException("Error processing movie data: " + e.getMessage(), e);
        }
    }
}