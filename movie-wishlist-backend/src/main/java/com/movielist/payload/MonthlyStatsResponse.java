package com.movielist.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsResponse {
    private String month; // Format: YYYY-MM
    private Long moviesWatched;
    private Long watchTime;
    private Double averageRating;
}