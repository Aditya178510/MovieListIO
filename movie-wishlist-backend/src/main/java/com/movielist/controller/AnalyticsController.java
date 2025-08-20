package com.movielist.controller;

import com.movielist.entity.User;
import com.movielist.exception.ApiException;
import com.movielist.exception.ResourceNotFoundException;
import com.movielist.payload.AnalyticsResponse;
import com.movielist.payload.GenreStatsResponse;
import com.movielist.payload.MonthlyStatsResponse;
import com.movielist.repository.UserRepository;
import com.movielist.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponse> getCurrentUserAnalytics() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
            
            AnalyticsResponse analytics = analyticsService.getUserAnalytics(user.getId());
            return ResponseEntity.ok(analytics);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving current user analytics", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving analytics");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AnalyticsResponse> getUserAnalytics(@PathVariable Long userId) {
        try {
            AnalyticsResponse analytics = analyticsService.getUserAnalytics(userId);
            return ResponseEntity.ok(analytics);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving analytics for user ID: {}", userId, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user analytics");
        }
    }

    @GetMapping("/genres/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<GenreStatsResponse>> getCurrentUserGenreStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
            
            List<GenreStatsResponse> genreStats = analyticsService.getUserGenreStats(user.getId());
            return ResponseEntity.ok(genreStats);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving current user genre stats", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving genre statistics");
        }
    }

    @GetMapping("/genres/user/{userId}")
    public ResponseEntity<List<GenreStatsResponse>> getUserGenreStats(@PathVariable Long userId) {
        try {
            List<GenreStatsResponse> genreStats = analyticsService.getUserGenreStats(userId);
            return ResponseEntity.ok(genreStats);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving genre stats for user ID: {}", userId, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving genre statistics");
        }
    }

    @GetMapping("/monthly/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyStatsResponse>> getCurrentUserMonthlyStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
            
            List<MonthlyStatsResponse> monthlyStats = analyticsService.getUserMonthlyStats(user.getId());
            return ResponseEntity.ok(monthlyStats);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving current user monthly stats", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving monthly statistics");
        }
    }

    @GetMapping("/monthly/user/{userId}")
    public ResponseEntity<List<MonthlyStatsResponse>> getUserMonthlyStats(@PathVariable Long userId) {
        try {
            List<MonthlyStatsResponse> monthlyStats = analyticsService.getUserMonthlyStats(userId);
            return ResponseEntity.ok(monthlyStats);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving monthly stats for user ID: {}", userId, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving monthly statistics");
        }
    }

    @GetMapping("/global")
    public ResponseEntity<AnalyticsResponse> getGlobalAnalytics() {
        try {
            AnalyticsResponse globalAnalytics = analyticsService.getGlobalAnalytics();
            return ResponseEntity.ok(globalAnalytics);
        } catch (Exception e) {
            logger.error("Error retrieving global analytics", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving global analytics");
        }
    }

    @GetMapping("/global/genres")
    public ResponseEntity<List<GenreStatsResponse>> getGlobalGenreStats() {
        try {
            List<GenreStatsResponse> globalGenreStats = analyticsService.getGlobalGenreStats();
            return ResponseEntity.ok(globalGenreStats);
        } catch (Exception e) {
            logger.error("Error retrieving global genre stats", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving global genre statistics");
        }
    }

    @GetMapping("/global/monthly")
    public ResponseEntity<List<MonthlyStatsResponse>> getGlobalMonthlyStats() {
        try {
            List<MonthlyStatsResponse> globalMonthlyStats = analyticsService.getGlobalMonthlyStats();
            return ResponseEntity.ok(globalMonthlyStats);
        } catch (Exception e) {
            logger.error("Error retrieving global monthly stats", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving global monthly statistics");
        }
    }
}