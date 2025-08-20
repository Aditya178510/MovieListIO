package com.movielist.service;

import com.movielist.entity.Badge;
import com.movielist.entity.Follow;
import com.movielist.entity.User;
import com.movielist.exception.ResourceNotFoundException;
import com.movielist.payload.UserProfileRequest;
import com.movielist.payload.UserProfileResponse;
import com.movielist.repository.BadgeRepository;
import com.movielist.repository.FollowRepository;
import com.movielist.repository.MovieRepository;
import com.movielist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return convertToUserProfileResponse(user);
    }

    public UserProfileResponse updateUserProfile(String username, UserProfileRequest profileRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        if (profileRequest.getFavoriteGenre() != null) {
            user.setFavoriteGenre(profileRequest.getFavoriteGenre());
        }
        
        if (profileRequest.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(profileRequest.getProfilePictureUrl());
        }
        
        if (profileRequest.getEmail() != null) {
            user.setEmail(profileRequest.getEmail());
        }
        
        if (profileRequest.getPassword() != null && !profileRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(profileRequest.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToUserProfileResponse(updatedUser);
    }

    public void followUser(String followerUsername, String followingUsername) {
        if (followerUsername.equals(followingUsername)) {
            throw new RuntimeException("You cannot follow yourself");
        }
        
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", followerUsername));
        
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", followingUsername));
        
        if (followRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId())) {
            throw new RuntimeException("You are already following this user");
        }
        
        Follow follow = new Follow();
        follow.setFollowerId(follower.getId());
        follow.setFollowingId(following.getId());
        
        followRepository.save(follow);
    }

    public void unfollowUser(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", followerUsername));
        
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", followingUsername));
        
        Follow follow = followRepository.findByFollowerIdAndFollowingId(follower.getId(), following.getId())
                .orElseThrow(() -> new RuntimeException("You are not following this user"));
        
        followRepository.delete(follow);
    }

    public List<UserProfileResponse> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Long> followerIds = followRepository.findFollowerIdsByFollowingId(user.getId());
        List<User> followers = userRepository.findAllById(followerIds);
        
        return followers.stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    public List<UserProfileResponse> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(user.getId());
        List<User> following = userRepository.findAllById(followingIds);
        
        return following.stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    public List<UserProfileResponse> getLeaderboard() {
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(this::convertToUserProfileResponse)
                .sorted((u1, u2) -> Long.compare(u2.getMoviesWatchedCount(), u1.getMoviesWatchedCount()))
                .limit(10) // Top 10 users
                .collect(Collectors.toList());
    }

    private UserProfileResponse convertToUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFavoriteGenre(user.getFavoriteGenre());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        response.setRole(user.getRole());
        
        // Count movies watched and total watch time
        response.setMoviesWatchedCount(movieRepository.countWatchedMoviesByUserId(user.getId()));
        response.setTotalWatchTime(movieRepository.calculateTotalWatchTimeByUserId(user.getId()));
        
        // Count followers and following
        response.setFollowersCount(followRepository.countFollowersByUserId(user.getId()));
        response.setFollowingCount(followRepository.countFollowingByUserId(user.getId()));
        
        // Get badges
        List<String> badges = badgeRepository.findByUserId(user.getId()).stream()
                .map(Badge::getBadgeName)
                .collect(Collectors.toList());
        response.setBadges(badges);
        
        // Check if current user is following this user
        // This will be set in the controller if needed
        response.setIsFollowing(false);
        
        return response;
    }
}