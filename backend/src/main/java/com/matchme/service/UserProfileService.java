package com.matchme.service;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.GameProfile;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.HashSet;
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

            if (profileDto.getCompetitiveness() != null) profile.setCompetitiveness(profileDto.getCompetitiveness());
            if (profileDto.getVoiceChatPreference() != null) profile.setVoiceChatPreference(profileDto.getVoiceChatPreference());
            if (profileDto.getPlaySchedule() != null) profile.setPlaySchedule(profileDto.getPlaySchedule());
            if (profileDto.getMainGoal() != null) profile.setMainGoal(profileDto.getMainGoal());

            if (profileDto.getGames() != null) {
                profile.getGames().clear();

                profileDto.getGames().forEach((gameName, gameDto) -> {
                    if (!VALID_GAMES.contains(gameName)) {
                        throw new IllegalArgumentException("We ran into error: Invalid game");
                    }
                    GameProfile game = new GameProfile();
                    game.setUserProfile(profile);
                    game.setGameName(gameName);

                    if (gameDto.getExpLvl() != null) {
                        if (!VALID_EXP_LVL.contains(gameDto.getExpLvl().trim())) {
                            throw new IllegalArgumentException("We ran into error: Invalid experience for: " + gameName);
                        }
                        game.setExpLvl(gameDto.getExpLvl().trim());
                    }
                    if (gameDto.getGamingHours() != null) {
                        if (!VALID_GAMING_HOURS.contains(gameDto.getGamingHours().trim())) {
                            throw new IllegalArgumentException("We ran into error: Invalid gaming hour level for: " + gameName );
                        }
                        game.setGamingHours(gameDto.getGamingHours().trim());

                    }
                    if (gameDto.getPreferredServers() != null && !gameDto.getPreferredServers().isEmpty() ) {
                            for (String server : gameDto.getPreferredServers()) {
                                if (server == null || server.isBlank()) {
                                    throw new IllegalArgumentException("We ran into error: Preferred serves must not be empty for game: " + gameName);
                                }
                                if (!VALID_SERVERS.contains(server)) {
                                    throw new IllegalArgumentException("We ran into error: Incorrect server for game: " + gameName);
                                }
                            }
                        game.setPreferredServersSet(gameDto.getPreferredServers());
                    }else {
                        game.setPreferredServersSet(new HashSet<>());
                    }

                    if(gameDto.getCurrentRank() != null){
                        if(!VALID_RANK.contains(gameDto.getCurrentRank().trim())){
                            throw new IllegalArgumentException("We ran into error: Invalid argument for game rank: " + gameName);
                        }
                        game.setCurrentRank(gameDto.getCurrentRank().trim());
                    }
                    profile.getGames().add(game);

                });
                
            }


                return userProfileRepository.save(profile);
            }

            @Transactional
            public UserProfile findByUserId(Long userId) {
                return userProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));
            }
            
        }

