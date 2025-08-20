package com.movielist.payload;

import com.movielist.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String favoriteGenre;
    private String profilePictureUrl;
    private User.Role role;
    private Long moviesWatchedCount;
    private Long totalWatchTime;
    private Long followersCount;
    private Long followingCount;
    private List<String> badges;
    private Boolean isFollowing; // Whether the current user is following this user
}