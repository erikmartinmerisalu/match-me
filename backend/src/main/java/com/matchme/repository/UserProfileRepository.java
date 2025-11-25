package com.matchme.repository;

import com.matchme.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByUserEmail(String email);
    Optional<UserProfile> findByProfilePic(String profilePic);
    
    @Query("SELECT up FROM UserProfile up WHERE up.profileCompleted = true AND up.user.id != :userId")
    List<UserProfile> findCompletedProfilesExcludingUser(@Param("userId") Long userId);
    
}