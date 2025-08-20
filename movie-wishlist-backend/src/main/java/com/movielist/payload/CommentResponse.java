package com.movielist.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Instant createdAt;
    private Long userId;
    private String username;
    private String userProfilePictureUrl;
    private Long movieId;
    private String movieTitle;
}