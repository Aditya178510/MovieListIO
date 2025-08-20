package com.movielist.controller;

import com.movielist.entity.User;
import com.movielist.exception.ApiException;
import com.movielist.exception.ResourceNotFoundException;
import com.movielist.payload.ApiResponse;
import com.movielist.payload.UserProfileRequest;
import com.movielist.payload.UserProfileResponse;
import com.movielist.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserProfileResponse userProfile = userService.getUserProfile(auth.getName());
            return ResponseEntity.ok(userProfile);
        } catch (ResourceNotFoundException e) {
            logger.error("User profile not found", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving current user profile", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user profile");
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        try {
            UserProfileResponse userProfile = userService.getUserProfile(username);
            return ResponseEntity.ok(userProfile);
        } catch (ResourceNotFoundException e) {
            logger.error("User profile not found for username: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving user profile for username: {}", username, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving user profile");
        }
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@Valid @RequestBody UserProfileRequest profileRequest) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserProfileResponse updatedProfile = userService.updateUserProfile(auth.getName(), profileRequest);
            logger.info("User profile updated successfully for: {}", auth.getName());
            return ResponseEntity.ok(updatedProfile);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found during profile update", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating user profile");
        }
    }

    @PostMapping("/follow/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> followUser(@PathVariable String username) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            userService.followUser(auth.getName(), username);
            logger.info("User {} is now following {}", auth.getName(), username);
            return ResponseEntity.ok(new ApiResponse(true, "You are now following " + username));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found during follow operation: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error following user: {}", username, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error following user");
        }
    }

    @PostMapping("/unfollow/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable String username) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            userService.unfollowUser(auth.getName(), username);
            logger.info("User {} has unfollowed {}", auth.getName(), username);
            return ResponseEntity.ok(new ApiResponse(true, "You have unfollowed " + username));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found during unfollow operation: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error unfollowing user: {}", username, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error unfollowing user");
        }
    }

    @GetMapping("/followers")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getFollowers() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<UserProfileResponse> followers = userService.getFollowers(auth.getName());
            return ResponseEntity.ok(followers);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found when retrieving followers", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving followers", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving followers");
        }
    }

    @GetMapping("/following")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getFollowing() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<UserProfileResponse> following = userService.getFollowing(auth.getName());
            return ResponseEntity.ok(following);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found when retrieving following users", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving following users", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving following users");
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserProfileResponse>> getLeaderboard() {
        try {
            List<UserProfileResponse> leaderboard = userService.getLeaderboard();
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            logger.error("Error retrieving leaderboard", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving leaderboard");
        }
    }
}