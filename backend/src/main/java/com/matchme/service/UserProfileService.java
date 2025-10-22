package com.matchme.service;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.GameProfile;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
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
                "Game1", "Game2", "Game3", "Game4", "Game5"
            );
            private static final Set<String> VALID_COMPETITIVENESS = Set.of(
                "Just for fun", "Casual", "Semi-competitive", "Highly competitive"
            );
            private static final Set<String> VALID_VOICE_CHAT_PREFERENCES = Set.of(
                "Always", "Sometimes", "Rarely", "Never"
            );
            private static final Set<String> VALID_PLAY_SCHEDULE = Set.of(
            "Weekday mornings", "Weekday evenings", "Weekend mornings", "Weekend evenings", "Late nights"
            );
            private static final Set<String> VALID_MAIN_GOAL = Set.of(
            "Rank climbing", "Learning", "Making friends", "Casual fun"
            ); 
            private static final Set<String> VALID_RANK = Set.of(
            "Unranked", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "N/A"
            );    

            @Transactional
            public UserProfile updateCurrentUserProfile(Long userId, UserProfileDto profileDto) {
            UserProfile profile = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            Map<String, String> errors = new HashMap<>();

            if (profileDto.getDisplayName() != null) {
                if (profileDto.getDisplayName().isBlank()) {
                    throw new IllegalArgumentException("Display name must not be blank");
                }
                else profile.setDisplayName(profileDto.getDisplayName());
            }

            if (profileDto.getAboutMe() != null){
                if(profileDto.getLookingFor().length() > 500){
                    throw new IllegalArgumentException("About me must be 500 characters long");
                }
                profile.setAboutMe(profileDto.getAboutMe().trim());
            }
            if (profileDto.getLookingFor() != null){
                if(profileDto.getLookingFor().trim().length() > 500){
                    throw new IllegalArgumentException("About me must be 500 characters long");
                }
            profile.setLookingFor(profileDto.getLookingFor().trim()); 
            } 
            if (profileDto.getBirthDate() != null){
                profile.setBirthDate(profileDto.getBirthDate());
            }
            if (profileDto.getPreferredAgeMin() != null) {
                if (profileDto.getPreferredAgeMin() < 0 || profileDto.getPreferredAgeMin() > 100){ 
                    throw new IllegalArgumentException("Minimum preferred age must be greater than 0 and lower than 100");
                }if(profileDto.getPreferredAgeMin() > profileDto.getPreferredAgeMin() ){
                    throw new IllegalArgumentException("Minimum preferred age can't be greater than maximum preferred age");
                }else{
                    profile.setPreferredAgeMin(profileDto.getPreferredAgeMin());
                }
            }
            if (profileDto.getPreferredAgeMax() != null) {
                if (profileDto.getPreferredAgeMax() < 0 || profileDto.getPreferredAgeMax() > 100)
                    throw new IllegalArgumentException("Maximum preferred age must be lower than 100");
                if(profileDto.getPreferredAgeMin() > profileDto.getPreferredAgeMin() ){
                    throw new IllegalArgumentException("Minimum preferred age can't be greater than maximum preferred age");
                }else{
                    profile.setPreferredAgeMax(profileDto.getPreferredAgeMax());
                }
            }
            if (profileDto.getMaxPreferredDistance() != null){
                if(profileDto.getMaxPreferredDistance() <5 || profileDto.getMaxPreferredDistance() > 200){
                    throw new IllegalArgumentException("Preferred distance must be over 5 km and less than 200km");
                }
                profile.setMaxPreferredDistance(profileDto.getMaxPreferredDistance());
            }
            if (profileDto.getTimezone() != null) profile.setTimezone(profileDto.getTimezone());
            if (profileDto.getProfilePic() != null) profile.setProfilePic(profileDto.getProfilePic());
            if (profileDto.getLatitude() != null) profile.setLatitude(profileDto.getLatitude());
            if (profileDto.getLongitude() != null) profile.setLongitude(profileDto.getLongitude());
            if (profileDto.getLocation() != null) profile.setLocation(profileDto.getLocation());

            if (profileDto.getGames() != null) {
                profile.getGames().clear();

                profileDto.getGames().forEach((gameName, gameDto) -> {
                    if (!VALID_GAMES.contains(gameName)) {
                        throw new IllegalArgumentException("We ran into error: Invalid game");
                    }
                    if (gameDto.getExpLvl() != null) {
                        if (!VALID_EXP_LVL.contains(gameDto.getExpLvl().trim())) {
                            throw new IllegalArgumentException("We ran into error: Invalid experience for: " + gameName);
                        }
                    }
                    if (gameDto.getGamingHours() != null) {
                        if (!VALID_GAMING_HOURS.contains(gameDto.getGamingHours().trim())) {
                            throw new IllegalArgumentException("We ran into error: Invalid gaming hour level for: " + gameName );
                        }
                    }
                    if (gameDto.getPreferredServers() != null  ) {
                        if(gameDto.getPreferredServers().isEmpty()){
                            throw new IllegalArgumentException("We ran into error: Servers not chosen for game: " + gameName);
                        }else {
                            for (String server : gameDto.getPreferredServers()) {
                                if (server == null || server.isBlank()) {
                                    throw new IllegalArgumentException("We ran into error: Preferred serves must not be empty for game: " + gameName);
                                }
                                if (!VALID_SERVERS.contains(server)) {
                                    throw new IllegalArgumentException("We ran into error: Incorrect server for game: " + gameName);
                                }
                            }
                        }
                    }
                    if(gameDto.getCompetitiveness() != null){
                        if(!VALID_COMPETITIVENESS.contains(gameDto.getCompetitiveness().trim())){
                            throw new IllegalArgumentException("We ran into error: Invalid argument for game competitiveness: " + gameName);
                        }
                    } 
                    if(gameDto.getVoiceChatPreference() != null){
                        if(!VALID_VOICE_CHAT_PREFERENCES.contains(gameDto.getVoiceChatPreference().trim())){
                            throw new IllegalArgumentException("We ran into error:Invalid argument for game voice chat preference: " + gameName);
                        }
                    }
                    if(gameDto.getPlaySchedule() != null){
                        if(!VALID_PLAY_SCHEDULE.contains(gameDto.getPlaySchedule().trim())){
                            throw new IllegalArgumentException(" We ran into error: Invalid argument for gaming schedule preference: " + gameName);
                        }
                    }
                    if(gameDto.getMainGoal() != null){
                        if(!VALID_MAIN_GOAL.contains(gameDto.getMainGoal().trim())){
                            throw new IllegalArgumentException("We ran into error: Invalid argument for game main goal: " + gameName);
                        }
                    }
                    if(gameDto.getCurrentRank() != null){
                        if(!VALID_RANK.contains(gameDto.getCurrentRank().trim())){
                            throw new IllegalArgumentException("We ran into error: Invalid argument for game rank: " + gameName);
                        }
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

            @Transactional
            public UserProfile findByUserId(Long userId) {
                return userProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));
            }
            
        }

