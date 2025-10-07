

    package com.matchme.service;

    import com.matchme.entity.GameProfile;
    import com.matchme.entity.UserProfile;
    import com.matchme.repository.UserProfileRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;


    import java.util.Set;

import javax.management.RuntimeErrorException;

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

            if(profile.getGames() == null || profile.getGames().isEmpty() ){
                throw new RuntimeException("Please add one game!");
            }

            for (GameProfile game : profile.getGames()) {
                if (!VALID_GAMES.contains(game.getGameName())) {
                    throw new RuntimeException("Server error: Invalid game name: " + game.getGameName());
                }
                if (!VALID_EXP_LVL.contains(game.getExpLvl())) {
                    throw new RuntimeException("Invalid experience level for game: " + game.getGameName());
                }
                if (!VALID_GAMING_HOURS.contains(game.getGamingHours())) {
                    throw new RuntimeException("Invalid gaming hours for game: " + game.getGameName());
                }
                if (game.getPreferredServersSet() == null 
                    || game.getPreferredServersSet().isEmpty()
                    || !VALID_SERVERS.containsAll(game.getPreferredServersSet())) {
                    throw new RuntimeException("Invalid preferred servers for game: " + game.getGameName());
                }
            }
            
            Integer maxDistance = profile.getMaxPreferredDistance();
            if (maxDistance == null || maxDistance > 200 || maxDistance < 1) {
                throw new RuntimeException("Profile distance must be less than 200km and more than 1 km");
            }

            return userProfileRepository.save(profile);
        }

        // FIXED: return UserProfile directly instead of Optional
        public UserProfile findByUserId(Long userId) {
            return userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));
        }
    }

