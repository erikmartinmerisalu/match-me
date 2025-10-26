package com.matchme.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.GameProfile;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
            public void updateProfile(Long userId, JsonNode json) {
                UserProfile profile = userProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

                    System.out.println("Received JSON: " + json.toPrettyString());

                if(json.has("displayName")) {
                    JsonNode nameNode = json.get("displayName");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Username is missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Username is missing!");
                    }

                    if(value.length() < 3){
                        throw new IllegalArgumentException("Username must be at least 3 characthers long");
                    }

                    if(value.length() > 25){
                        throw new IllegalArgumentException("Username must be less than 25 characthers long");
                    }

                    profile.setDisplayName(value);
                }

                if(json.has("aboutMe")){
                    JsonNode nameNode = json.get("aboutMe");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("About me field missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("About me name must not be blank");
                    }

                    if(value.length() > 250){
                        throw new IllegalArgumentException("About me must be less than 250 characthers");
                    }

                    profile.setAboutMe(value);
                }
                if(json.has("lookingFor")){
                    JsonNode nameNode = json.get("lookingFor");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Looking for field missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Looking for field must not be blank");
                    }

                    if(value.length() > 250){
                        throw new IllegalArgumentException("Looking for field field must be less than 250 characthers");
                    }
                    profile.setLookingFor(value);

                }

                if(json.has("birthDate")){
                    JsonNode nameNode = json.get("birthDate");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Birthdate missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Birthdate missing!");
                    }

                    LocalDate birthDate;
                    try {
                        birthDate = LocalDate.parse(value);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid birthdate format");
                    }

                    if (birthDate.isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException("Birthdate cannot be in the future");
                    }
                    System.out.print(birthDate);
                    profile.setBirthDate(birthDate);

                }

                if(json.has("games")) {
                    JsonNode nameNode = json.get("games");

                    if(!nameNode.isObject()){
                        throw new IllegalArgumentException("Server error");
                    }
                    if(nameNode == null || nameNode.isNull() || nameNode.size() == 0) {
                        throw new IllegalArgumentException("Choose at least one game!");
                    }

                    Map<String, GameProfile> existingGames = new HashMap<>();
                    for (GameProfile g : profile.getGames()) {
                        existingGames.put(g.getGameName(), g);
                    }

                    Iterator<String> gameKeys = nameNode.fieldNames();
                    while (gameKeys.hasNext()) {
                        String gameKey = gameKeys.next();
                        JsonNode gameDetails = nameNode.get(gameKey);

                        if (!VALID_GAMES.contains(gameKey)) {
                            throw new IllegalArgumentException("Invalid game key: " + gameKey);
                        }

                        GameProfile gameProfile;
                        if (existingGames.containsKey(gameKey)) {
                            gameProfile = existingGames.get(gameKey);
                        } else {
                            gameProfile = new GameProfile();
                            gameProfile.setGameName(gameKey);
                            gameProfile.setUserProfile(profile);
                            profile.getGames().add(gameProfile);
                        }

                        if (gameDetails != null && !gameDetails.isNull() ) {
                            if(gameDetails.has("expLvl")) {
                                String expLvl = gameDetails.get("expLvl").asText();
                                if (!VALID_EXP_LVL.contains(expLvl)) {
                                    throw new IllegalArgumentException("Invalid Experience level for " + gameKey + ": " + expLvl);
                                }
                                gameProfile.setExpLvl(expLvl);
                            }

                            if(gameDetails.has("gamingHours")) {
                                String hours = gameDetails.get("gamingHours").asText();
                                if (!VALID_GAMING_HOURS.contains(hours)) {
                                    throw new IllegalArgumentException("Server error: Invalid Gaming Hours for " + gameKey + ": " + hours);
                                }
                                gameProfile.setGamingHours(hours);
                            }

                            if(gameDetails.has("preferredServers")) {
                                JsonNode serversNode = gameDetails.get("preferredServers");
                                Set<String> servers = new HashSet<>();
                                for (JsonNode serverNode : serversNode) {
                                    String server = serverNode.asText();
                                    if(!VALID_SERVERS.contains(server)) {
                                        throw new IllegalArgumentException("Server error: Invalid server in " + gameKey + ": " + server);
                                    }
                                    servers.add(server);
                                }
                                gameProfile.setPreferredServersSet(servers);
                            }

                            if(gameDetails.has("currentRank")) {
                                String rank = gameDetails.get("currentRank").asText();
                                if(!VALID_RANK.contains(rank)) {
                                    throw new IllegalArgumentException("Server error: Invalid rank for " + gameKey + ": " + rank);
                                }
                                gameProfile.setCurrentRank(rank);
                            }
                        }
                    }
                }

                if(json.has("competitiveness")){
                    JsonNode nameNode = json.get("competitiveness");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Competitiveness missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Competitiveness missing!");
                    }

                    if(!VALID_COMPETITIVENESS.contains(value)){
                        throw new IllegalArgumentException("Server error: Invalid rank competitiveness");
                    }
                    profile.setCompetitiveness(value);

                }

                if(json.has("voiceChatPreference")){
                    JsonNode nameNode = json.get("voiceChatPreference");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Voice Chat Preference missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Voice Chat Preference missing!");
                    }

                    if(!VALID_VOICE_CHAT_PREFERENCES.contains(value)){
                        throw new IllegalArgumentException("Server error: Invalid Voice Chat Preference");
                    }
                    profile.setVoiceChatPreference(value);
                }

                if(json.has("playSchedule")){
                    JsonNode nameNode = json.get("playSchedule");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Play Schedule missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Play Schedule missing!");
                    }

                    if(!VALID_PLAY_SCHEDULE.contains(value)){
                        throw new IllegalArgumentException("Server error: Invalid Play Schedule");
                    }
                    profile.setPlaySchedule(value);

                }

                if(json.has("mainGoal")){
                    JsonNode nameNode = json.get("mainGoal");
                    if(nameNode.isNull()) {
                        throw new IllegalArgumentException("Main Goal missing!");
                    }

                    String value = nameNode.asText().trim();
                    if(value.isBlank()) {
                        throw new IllegalArgumentException("Main Goal missing!");
                    }

                    if(!VALID_MAIN_GOAL.contains(value)){
                        throw new IllegalArgumentException("Server error: Invalid Main Goal");
                    }
                    profile.setMainGoal(value);
                }

                Integer preferredAgeMin = profile.getPreferredAgeMin();
                Integer preferredAgeMax = profile.getPreferredAgeMax();

                if(json.has("preferredAgeMin")){
                    JsonNode node = json.get("preferredAgeMin");
                    if(node.isNull()){
                        throw new IllegalArgumentException("Preferred Minimum Age  missing!");
                    }
                    int value = node.asInt(-1);
                    if(value < 0){
                        throw new IllegalArgumentException("Preferred Minimum Age must be >= 0");
                    }
                    profile.setPreferredAgeMin(value);
                    preferredAgeMin = value;
                }

                if(json.has("preferredAgeMax")){
                    JsonNode node = json.get("preferredAgeMax");
                    if(node.isNull()){
                        throw new IllegalArgumentException("Preferred Maximum Age missing!");
                    }
                    int value = node.asInt(-1);
                    if(value < 0){
                        throw new IllegalArgumentException("Preferred Maximum Age must be >= 0");
                    }
                    profile.setPreferredAgeMax(value);
                    preferredAgeMax = value;
                }

                if(preferredAgeMin != null && preferredAgeMax != null && preferredAgeMin > preferredAgeMax){
                    throw new IllegalArgumentException("Preferred Minimum Age cannot be greater than Preferred Maximum Age");
                }

                if(json.has("location")){
                    JsonNode node = json.get("location");
                    if(node.isNull()){
                        throw new IllegalArgumentException("Location missing!");
                    }
                    String value = node.asText().trim();
                    if(value.isBlank()){
                        throw new IllegalArgumentException("Location must not be blank");
                    }
                    profile.setLocation(value);
                }

                if(json.has("latitude")){
                    JsonNode node = json.get("latitude");
                    if(node.isNull()){
                        throw new IllegalArgumentException("Latitude missing!");
                    }
                    double value = node.asDouble(Double.NaN);
                    if(Double.isNaN(value)){
                        throw new IllegalArgumentException("Invalid latitude");
                    }
                    profile.setLatitude(value);
                }

                if(json.has("longitude")){
                    JsonNode node = json.get("longitude");
                    if(node.isNull()){
                        throw new IllegalArgumentException("Longitude missing!");
                    }
                    double value = node.asDouble(Double.NaN);
                    if(Double.isNaN(value)){
                        throw new IllegalArgumentException("Invalid longitude");
                    }
                    profile.setLongitude(value);
                }

                if(json.has("timezone")){
                    JsonNode node = json.get("timezone");
                    if(node.isNull()){
                        throw new IllegalArgumentException("Server error: Timezone missing! Try again!");
                    }
                    String value = node.asText().trim();
                    if(value.isBlank()){
                        throw new IllegalArgumentException("Server error: Timezone must not be blank");
                    }
                    profile.setTimezone(value);
                }
                

                userProfileRepository.save(profile);
            }




            @Transactional
            public UserProfile findByUserId(Long userId) {
                UserProfile profile = userProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));
                profile.getGames().size();
                return profile;
            }
            
            @Transactional
            public UserProfile saveProfile(UserProfile profile) {
                return userProfileRepository.save(profile);
            }

        }

