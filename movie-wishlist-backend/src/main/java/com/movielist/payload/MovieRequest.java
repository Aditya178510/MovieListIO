package com.movielist.payload;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MovieRequest {
    @NotBlank
    private String title;
    
    private String genre;
    
    private Integer releaseYear;
    
    private Integer runtime;
    
    private String posterUrl;
    
    private Integer rating;
    
    private String review;
}