package com.movielist.service;

import com.movielist.entity.Badge;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.repository.BadgeRepository;
import com.movielist.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private MovieRepository movieRepository;

    public void checkAndAwardBadges(User user) {
        // Count watched movies
        Long watchedMoviesCount = movieRepository.countWatchedMoviesByUserId(user.getId());
        
        // First Movie Watched Badge
        if (watchedMoviesCount >= 1 && !badgeRepository.existsByUserAndBadgeName(user, "First Movie Watched")) {
            awardBadge(user, "First Movie Watched");
        }
        
        // 5 Movies Watched Badge
        if (watchedMoviesCount >= 5 && !badgeRepository.existsByUserAndBadgeName(user, "5 Movies Watched")) {
            awardBadge(user, "5 Movies Watched");
        }
        
        // 10 Movies Watched Badge
        if (watchedMoviesCount >= 10 && !badgeRepository.existsByUserAndBadgeName(user, "10 Movies Watched")) {
            awardBadge(user, "10 Movies Watched");
        }
        
        // 25 Movies Watched Badge
        if (watchedMoviesCount >= 25 && !badgeRepository.existsByUserAndBadgeName(user, "25 Movies Watched")) {
            awardBadge(user, "25 Movies Watched");
        }
        
        // 50 Movies Watched Badge
        if (watchedMoviesCount >= 50 && !badgeRepository.existsByUserAndBadgeName(user, "50 Movies Watched")) {
            awardBadge(user, "50 Movies Watched");
        }
        
        // 100 Movies Watched Badge
        if (watchedMoviesCount >= 100 && !badgeRepository.existsByUserAndBadgeName(user, "100 Movies Watched")) {
            awardBadge(user, "100 Movies Watched");
        }
        
        // Check total watch time
        Long totalWatchTime = movieRepository.calculateTotalWatchTimeByUserId(user.getId());
        if (totalWatchTime != null) {
            // 24 Hours Watched Badge (1440 minutes)
            if (totalWatchTime >= 1440 && !badgeRepository.existsByUserAndBadgeName(user, "24 Hours Watched")) {
                awardBadge(user, "24 Hours Watched");
            }
            
            // 100 Hours Watched Badge (6000 minutes)
            if (totalWatchTime >= 6000 && !badgeRepository.existsByUserAndBadgeName(user, "100 Hours Watched")) {
                awardBadge(user, "100 Hours Watched");
            }
        }
    }
    
    private void awardBadge(User user, String badgeName) {
        Badge badge = new Badge();
        badge.setUser(user);
        badge.setBadgeName(badgeName);
        badgeRepository.save(badge);
    }
}