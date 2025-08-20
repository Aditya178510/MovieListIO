package com.movielist.payload;

import com.movielist.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private Long id;
    private String title;
    private String genre;
    private Integer releaseYear;
    private Integer runtime;
    private String posterUrl;
    private Movie.Status status;
    private Integer rating;
    private String review;
    private Long userId;
    private String username;
    private Long likesCount;
    private Long commentsCount;
    private Boolean userLiked;
}