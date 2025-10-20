

package com.matchme.service;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.GameProfile;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
        public UserProfile updateCurrentUserProfile(String userEmail, UserProfileDto profileDto) {
       UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Map<String, String> errors = new HashMap<>();

        // ---- BASIC PROFILE FIELDS ----
        if (profileDto.getDisplayName() != null) {
            if (profileDto.getDisplayName().isBlank()) errors.put("displayName", "Display name must not be blank");
            else profile.setDisplayName(profileDto.getDisplayName());
        }

        if (profileDto.getAboutMe() != null) profile.setAboutMe(profileDto.getAboutMe());
        if (profileDto.getLookingFor() != null) profile.setLookingFor(profileDto.getLookingFor());
        if (profileDto.getBirthDate() != null) profile.setBirthDate(profileDto.getBirthDate());
        if (profileDto.getPreferredAgeMin() != null) {
        if (profileDto.getPreferredAgeMin() < 18 || profileDto.getPreferredAgeMin() > 99)
            errors.put("preferredAgeMin", "Must be between 18 and 99");
        }
        else profile.setPreferredAgeMin(profileDto.getPreferredAgeMin());{ 
        }
        if (profileDto.getPreferredAgeMax() != null) {
            if (profileDto.getPreferredAgeMax() < 18 || profileDto.getPreferredAgeMax() > 99)
                errors.put("preferredAgeMax", "Must be between 18 and 99");
            else profile.setPreferredAgeMax(profileDto.getPreferredAgeMax());
        }
        if (profileDto.getMaxPreferredDistance() != null) profile.setMaxPreferredDistance(profileDto.getMaxPreferredDistance());
        if (profileDto.getTimezone() != null) profile.setTimezone(profileDto.getTimezone());
        if (profileDto.getProfilePic() != null) profile.setProfilePic(profileDto.getProfilePic());
        if (profileDto.getLatitude() != null) profile.setLatitude(profileDto.getLatitude());
        if (profileDto.getLongitude() != null) profile.setLongitude(profileDto.getLongitude());
        if (profileDto.getLocation() != null) profile.setLocation(profileDto.getLocation());

        // ---- GAMES ----
        if (profileDto.getGames() != null) {
            profile.getGames().clear();
            profileDto.getGames().forEach((gameName, gameDto) -> {
                if (!VALID_GAMES.contains(gameName)) {
                    errors.put(gameName, "Invalid game");
                    return;
                }

                if (gameDto.getExpLvl() != null && !VALID_EXP_LVL.contains(gameDto.getExpLvl())) {
                    errors.put(gameName + ".expLvl", "Invalid experience level");
                }

                if (gameDto.getGamingHours() != null && !VALID_GAMING_HOURS.contains(gameDto.getGamingHours())) {
                    errors.put(gameName + ".gamingHours", "Invalid gaming hours");
                }

                if (gameDto.getPreferredServers() != null && !VALID_SERVERS.containsAll(gameDto.getPreferredServers())) {
                    errors.put(gameName + ".preferredServers", "Invalid servers selected");
                }

                // Only add game if there are no errors for this game
                if (!errors.keySet().stream().anyMatch(k -> k.startsWith(gameName))) {
                    GameProfile game = new GameProfile();
                    game.setGameName(gameName);
                    game.setExpLvl(gameDto.getExpLvl());
                    game.setGamingHours(gameDto.getGamingHours());
                    game.setPreferredServersSet(gameDto.getPreferredServers());
                    game.setCompetitiveness(gameDto.getCompetitiveness());
                    game.setVoiceChatPreference(gameDto.getVoiceChatPreference());
                    game.setPlaySchedule(gameDto.getPlaySchedule());
                    game.setMainGoal(gameDto.getMainGoal());
                    game.setCurrentRank(gameDto.getCurrentRank());
                    game.setUserProfile(profile);
                    profile.getGames().add(game);
                }
            });
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.toString());
        }

            return userProfileRepository.save(profile);
        }

        // FIXED: return UserProfile directly instead of Optional
        public UserProfile findByUserId(Long userId) {
            return userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));
        }
         
    }

