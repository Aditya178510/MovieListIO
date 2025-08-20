package com.movielist.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    
    @Size(max = 100)
    private String favoriteGenre;
    
    @Size(max = 255)
    private String profilePictureUrl;
    
    @Size(min = 6, max = 100)
    private String password;
    
    @Email
    @Size(max = 100)
    private String email;
}