package com.movielist.repository;

import com.movielist.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFollowingId(Long followingId);
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followingId = :userId")
    Long countFollowersByUserId(Long userId);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followerId = :userId")
    Long countFollowingByUserId(Long userId);

    @Query("SELECT f FROM Follow f WHERE f.followerId = :followerId AND f.followingId = :followingId")
    java.util.Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Query("SELECT f.followerId FROM Follow f WHERE f.followingId = :userId")
    List<Long> findFollowerIdsByFollowingId(Long userId);

    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :userId")
    List<Long> findFollowingIdsByFollowerId(Long userId);
}