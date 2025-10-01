package com.matchme.service;

import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    private static final Set<String> VALID_SERVERS = Set.of(
        "N-America", "S-America", "EU East", "EU West", "Asia", "AU+SEA", "Africa+Middle east"
    );

    private static final Set<String> VALID_GAMING_HOURS = Set.of(
        "<100", "101-500", "501-1000", "1000+"
    );

    private static final Set<String> VALID_EXP_LVL = Set.of(
        "Beginner", "Intermediate", "Advanced"
    );

    private static final Set<String> VALID_GAMES = Set.of(
        "Game1", "Game2", "Game3", "Game4", "Game5" // Replace with your actual game options
    );

    // SAVE PROFILE WITH VALIDATIONS
    public UserProfile saveProfile(UserProfile profile) {
        // Validate required profile fields
        if (profile.getDisplayName() == null || profile.getDisplayName().isBlank()) {
            throw new RuntimeException("Display name is required");
        }
        if (profile.getBirthDate() == null) {
            throw new RuntimeException("Birth date is required");
        }

        // Validate expLvl
        if (!VALID_EXP_LVL.contains(profile.getExpLvl())) {
            throw new RuntimeException("Invalid experience level");
        }

        // Validate gaming hours
        if (!VALID_GAMING_HOURS.contains(profile.getGamingHours())) {
            throw new RuntimeException("Invalid gaming hours");
        }

        // Validate servers (1 or 2)
        if (profile.getPreferredServers() == null 
                || profile.getPreferredServers().isEmpty()
                || profile.getPreferredServers().size() > 2
                || !VALID_SERVERS.containsAll(profile.getPreferredServers())) {
            throw new RuntimeException("Preferred servers must be 1 or 2 valid options");
        }

        // Validate games (up to 3)
        if (profile.getGames() == null 
                || profile.getGames().isEmpty() 
                || profile.getGames().size() > 3
                || !VALID_GAMES.containsAll(profile.getGames())) {
            throw new RuntimeException("Games must contain up to 3 valid options");
        }

        return userProfileRepository.save(profile);
    }

    // FIXED: return UserProfile directly instead of Optional
    public UserProfile findByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}
