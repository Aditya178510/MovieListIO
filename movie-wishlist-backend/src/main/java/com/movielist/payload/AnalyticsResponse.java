package com.movielist.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long totalMovies;
    private Long watchedMovies;
    private Long wishlistMovies;
    private Long totalWatchTime;
    private Double averageRating;
    private String favoriteGenre;
    private Long totalLikes;
    private Long totalComments;
    private Long totalFollowers;
    private Long totalFollowing;
}