package com.movielist.security;

import com.movielist.entity.User;
import com.movielist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with username: {}", username);
                        return new UsernameNotFoundException("User not found with username: " + username);
                    });

            logger.debug("User found with username: {}", username);
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }

    public UserDetails loadUserById(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with id: {}", id);
                        return new UsernameNotFoundException("User not found with id: " + id);
                    });

            logger.debug("User found with id: {}", id);
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (Exception e) {
            logger.error("Error loading user by id: {}", id, e);
            throw e;
        }
    }
}