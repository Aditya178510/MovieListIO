package com.movielist.service;

import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.exception.ResourceNotFoundException;
import com.movielist.payload.AnalyticsResponse;
import com.movielist.payload.GenreStatsResponse;
import com.movielist.payload.MonthlyStatsResponse;
import com.movielist.repository.CommentRepository;
import com.movielist.repository.FollowRepository;
import com.movielist.repository.LikeRepository;
import com.movielist.repository.MovieRepository;
import com.movielist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FollowRepository followRepository;

    public AnalyticsResponse getUserAnalytics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        List<Movie> allMovies = movieRepository.findByUser(user);
        List<Movie> watchedMovies = movieRepository.findByUserAndStatus(user, Movie.Status.WATCHED);
        List<Movie> wishlistMovies = movieRepository.findByUserAndStatus(user, Movie.Status.WISHLIST);
        
        AnalyticsResponse response = new AnalyticsResponse();
        response.setTotalMovies((long) allMovies.size());
        response.setWatchedMovies((long) watchedMovies.size());
        response.setWishlistMovies((long) wishlistMovies.size());
        response.setTotalWatchTime(calculateTotalWatchTime(watchedMovies));
        response.setAverageRating(calculateAverageRating(watchedMovies));
        response.setFavoriteGenre(findFavoriteGenre(watchedMovies));
        response.setTotalLikes(likeRepository.countByUser(user));
        response.setTotalComments(commentRepository.countByUser(user));
        response.setTotalFollowers(followRepository.countFollowersByUserId(userId));
        response.setTotalFollowing(followRepository.countFollowingByUserId(userId));
        
        return response;
    }

    public List<GenreStatsResponse> getUserGenreStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        List<Movie> watchedMovies = movieRepository.findByUserAndStatus(user, Movie.Status.WATCHED);
        
        return calculateGenreStats(watchedMovies);
    }

    public List<MonthlyStatsResponse> getUserMonthlyStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        List<Movie> watchedMovies = movieRepository.findByUserAndStatus(user, Movie.Status.WATCHED);
        
        return calculateMonthlyStats(watchedMovies);
    }

    public AnalyticsResponse getGlobalAnalytics() {
        List<Movie> allMovies = movieRepository.findAll();
        List<Movie> watchedMovies = allMovies.stream()
                .filter(movie -> movie.getStatus() == Movie.Status.WATCHED)
                .collect(Collectors.toList());
        List<Movie> wishlistMovies = allMovies.stream()
                .filter(movie -> movie.getStatus() == Movie.Status.WISHLIST)
                .collect(Collectors.toList());
        
        AnalyticsResponse response = new AnalyticsResponse();
        response.setTotalMovies((long) allMovies.size());
        response.setWatchedMovies((long) watchedMovies.size());
        response.setWishlistMovies((long) wishlistMovies.size());
        response.setTotalWatchTime(calculateTotalWatchTime(watchedMovies));
        response.setAverageRating(calculateAverageRating(watchedMovies));
        response.setFavoriteGenre(findFavoriteGenre(watchedMovies));
        response.setTotalLikes((long) likeRepository.count());
        response.setTotalComments((long) commentRepository.count());
        response.setTotalFollowers((long) followRepository.count()); // Same as total following
        response.setTotalFollowing((long) followRepository.count());
        
        return response;
    }

    public List<GenreStatsResponse> getGlobalGenreStats() {
        List<Movie> watchedMovies = movieRepository.findAll().stream()
                .filter(movie -> movie.getStatus() == Movie.Status.WATCHED)
                .collect(Collectors.toList());
        
        return calculateGenreStats(watchedMovies);
    }

    public List<MonthlyStatsResponse> getGlobalMonthlyStats() {
        List<Movie> watchedMovies = movieRepository.findAll().stream()
                .filter(movie -> movie.getStatus() == Movie.Status.WATCHED)
                .collect(Collectors.toList());
        
        return calculateMonthlyStats(watchedMovies);
    }

    private Long calculateTotalWatchTime(List<Movie> movies) {
        return movies.stream()
                .filter(movie -> movie.getRuntime() != null)
                .mapToLong(Movie::getRuntime)
                .sum();
    }

    private Double calculateAverageRating(List<Movie> movies) {
        return movies.stream()
                .filter(movie -> movie.getRating() != null)
                .mapToInt(Movie::getRating)
                .average()
                .orElse(0.0);
    }

    private String findFavoriteGenre(List<Movie> movies) {
        Map<String, Long> genreCounts = movies.stream()
                .filter(movie -> movie.getGenre() != null && !movie.getGenre().isEmpty())
                .collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()));
        
        return genreCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }

    private List<GenreStatsResponse> calculateGenreStats(List<Movie> movies) {
        long totalMovies = movies.size();
        
        // Group movies by genre and count
        Map<String, List<Movie>> moviesByGenre = movies.stream()
                .filter(movie -> movie.getGenre() != null && !movie.getGenre().isEmpty())
                .collect(Collectors.groupingBy(Movie::getGenre));
        
        List<GenreStatsResponse> genreStats = new ArrayList<>();
        
        for (Map.Entry<String, List<Movie>> entry : moviesByGenre.entrySet()) {
            String genre = entry.getKey();
            List<Movie> genreMovies = entry.getValue();
            long count = genreMovies.size();
            double percentage = totalMovies > 0 ? (double) count / totalMovies * 100 : 0;
            double averageRating = genreMovies.stream()
                    .filter(movie -> movie.getRating() != null)
                    .mapToInt(Movie::getRating)
                    .average()
                    .orElse(0.0);
            
            genreStats.add(new GenreStatsResponse(genre, count, percentage, averageRating));
        }
        
        // Sort by count in descending order
        genreStats.sort((g1, g2) -> g2.getCount().compareTo(g1.getCount()));
        
        return genreStats;
    }

    private List<MonthlyStatsResponse> calculateMonthlyStats(List<Movie> movies) {
        // Group movies by month (YYYY-MM)
        Map<String, List<Movie>> moviesByMonth = new HashMap<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (Movie movie : movies) {
            // Assuming the movie has a timestamp for when it was marked as watched
            // If not, we could use the movie's creation date or last updated date
            Instant timestamp = movie.getUpdatedAt(); // Assuming there's an updatedAt field
            if (timestamp == null) {
                timestamp = Instant.now(); // Fallback to current time if no timestamp
            }
            
            LocalDate date = timestamp.atZone(ZoneId.systemDefault()).toLocalDate();
            String monthKey = date.format(formatter);
            
            if (!moviesByMonth.containsKey(monthKey)) {
                moviesByMonth.put(monthKey, new ArrayList<>());
            }
            moviesByMonth.get(monthKey).add(movie);
        }
        
        List<MonthlyStatsResponse> monthlyStats = new ArrayList<>();
        
        for (Map.Entry<String, List<Movie>> entry : moviesByMonth.entrySet()) {
            String month = entry.getKey();
            List<Movie> monthMovies = entry.getValue();
            long moviesWatched = monthMovies.size();
            long watchTime = calculateTotalWatchTime(monthMovies);
            double averageRating = calculateAverageRating(monthMovies);
            
            monthlyStats.add(new MonthlyStatsResponse(month, moviesWatched, watchTime, averageRating));
        }
        
        // Sort by month in ascending order
        monthlyStats.sort(Comparator.comparing(MonthlyStatsResponse::getMonth));
        
        return monthlyStats;
    }
}