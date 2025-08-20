package com.movielist.repository;

import com.movielist.entity.Badge;
import com.movielist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUser(User user);
    List<Badge> findByUserId(Long userId);
    boolean existsByUserAndBadgeName(User user, String badgeName);
}