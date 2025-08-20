package com.movielist.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreStatsResponse {
    private String genre;
    private Long count;
    private Double percentage;
    private Double averageRating;
}