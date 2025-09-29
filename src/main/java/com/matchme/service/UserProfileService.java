// UserProfileService.java (updated)
package com.matchme.service;

import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile saveProfile(UserProfile profile) {
        // Check if profile is completed (has at least 5 biographical data points)
        int dataPoints = countBiographicalDataPoints(profile);
        profile.setProfileCompleted(dataPoints >= 5);
        
        return userProfileRepository.save(profile);
    }

    public Optional<UserProfile> findByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    private int countBiographicalDataPoints(UserProfile profile) {
        int count = 0;
        if (profile.getPreferredServers() != null && !profile.getPreferredServers().isEmpty()) count++;
        if (profile.getGames() != null && !profile.getGames().isEmpty()) count++;
        if (profile.getGamingHours() != null && !profile.getGamingHours().isEmpty()) count++;
        if (profile.getRank() != null && !profile.getRank().isEmpty()) count++;
        if (profile.getBirthDate() != null) count++;
        if (profile.getTimezone() != null && !profile.getTimezone().isEmpty()) count++;
        if (profile.getRegion() != null && !profile.getRegion().isEmpty()) count++;
        return count;
    }
}