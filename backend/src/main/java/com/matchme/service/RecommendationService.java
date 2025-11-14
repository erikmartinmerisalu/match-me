package com.matchme.service;

import com.matchme.dto.RecommendationDto;
import com.matchme.entity.GameProfile;
import com.matchme.entity.User;
import com.matchme.repository.ConnectionRepository;
import com.matchme.entity.Connection;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ConnectionRepository connectionRepository;

    // 40% baseline for passing deal-breakers + 60% from compatibility factors
    private static final double BASELINE_SCORE = 40.0;

    // NEW METHOD to filter out blocked users
    private List<UserProfile> filterOutBlockedUsers(Long currentUserId, List<UserProfile> allProfiles) {
        // Get all blocked connections for current user
        List<Connection> blockedConnections = connectionRepository.findBlockedConnectionsForUser(currentUserId);
        
        // Extract user IDs of blocked users (both directions)
        Set<Long> blockedUserIds = blockedConnections.stream()
                .map(connection -> {
                    if (connection.getFromUser().getId().equals(currentUserId)) {
                        return connection.getToUser().getId(); // Users I blocked
                    } else {
                        return connection.getFromUser().getId(); // Users who blocked me
                    }
                })
                .collect(Collectors.toSet());
        
        System.out.println("🚫 Filtering out " + blockedUserIds.size() + " blocked users for user " + currentUserId);
        
        // Filter out blocked users
        return allProfiles.stream()
                .filter(profile -> !blockedUserIds.contains(profile.getUser().getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(Long userId) {
        Optional<User> currentUserOpt = userService.findById(userId);

        if (currentUserOpt.isEmpty() || currentUserOpt.get().getProfile() == null) {
            return Collections.emptyList();
        }

        User currentUser = currentUserOpt.get();
        UserProfile currentProfile = currentUser.getProfile();
        
        if (!currentProfile.isProfileCompleted()) {
            return Collections.emptyList();
        }

        List<UserProfile> allProfiles = userProfileRepository.findCompletedProfilesExcludingUser(userId);
        
        // ADD THIS LINE: Filter out blocked users
        List<UserProfile> filteredProfiles = filterOutBlockedUsers(userId, allProfiles);
        
        System.out.println("📊 After filtering: " + filteredProfiles.size() + " potential matches (from " + allProfiles.size() + " total)");

        Map<UserProfile, CompatibilityResult> compatibilityMap = new HashMap<>();

        // CHANGE THIS LINE: Use filteredProfiles instead of allProfiles
        for (UserProfile candidateProfile : filteredProfiles) {
            Long candidateUserId = candidateProfile.getUser().getId();
            
            // This check might be redundant now with the filtering, but keeping for safety
            Optional<Connection> existingConnection = connectionRepository
                    .findConnectionBetweenUsers(userId, candidateUserId);
            
            if (existingConnection.isPresent()) {
                continue; 
            }

            if (!passesDealbreakers(currentProfile, candidateProfile)) {
                continue;
            }

            CompatibilityResult result = findCompatibleGamesWithScores(currentProfile, candidateProfile, 75.0);
            
            if (!result.compatibleGames.isEmpty()) {
                compatibilityMap.put(candidateProfile, result);
            }
        }

        // If we don't have enough recommendations with 75% threshold, try 50%
        if (compatibilityMap.size() < 3) {
            compatibilityMap.clear();
            
            // CHANGE THIS LINE: Use filteredProfiles instead of allProfiles
            for (UserProfile candidateProfile : filteredProfiles) {
                Long candidateUserId = candidateProfile.getUser().getId();
                
                // This check might be redundant now with the filtering, but keeping for safety
                Optional<Connection> existingConnection = connectionRepository
                        .findConnectionBetweenUsers(userId, candidateUserId);
                
                if (existingConnection.isPresent()) {
                    continue;
                }

                if (!passesDealbreakers(currentProfile, candidateProfile)) {
                    continue;
                }

                CompatibilityResult result = findCompatibleGamesWithScores(currentProfile, candidateProfile, 50.0);
                
                if (!result.compatibleGames.isEmpty()) {
                    compatibilityMap.put(candidateProfile, result);
                }
            }
        }

        // Sort by average compatibility score (highest first) and limit to 10
        List<RecommendationDto> recommendations = compatibilityMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().averageScore, e1.getValue().averageScore))
                .limit(10)
                .map(entry -> new RecommendationDto(
                        entry.getKey().getUser().getId(),
                        entry.getKey().getDisplayName(),
                        entry.getValue().compatibleGames
                ))
                .collect(Collectors.toList());

        System.out.println("✅ Final recommendations: " + recommendations.size() + " users");
        return recommendations;
    }

    // ... rest of your existing methods remain the same
    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendationsByEmail(String email) {
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }

        return getRecommendations(userOpt.get().getId());
    }

    @Transactional(readOnly = true)
    public List<Long> getTopRecommendationIds(String email, int limit) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }
    
        List<RecommendationDto> recommendations = getRecommendations(userOpt.get().getId());
    
        return recommendations.stream()
            .limit(limit)
            .map(RecommendationDto::getUserId)
            .collect(Collectors.toList());
    }

    // ... rest of your existing private methods remain exactly the same
    private static class CompatibilityResult {
        List<String> compatibleGames;
        double averageScore;

        CompatibilityResult(List<String> games, double avgScore) {
            this.compatibleGames = games;
            this.averageScore = avgScore;
        }
    }


    private boolean passesDealbreakers(UserProfile profile1, UserProfile profile2) {
        if (!hasCommonGameWithCommonServer(profile1, profile2)) {
            return false;
        }

        if (!withinPreferredDistance(profile1, profile2)) {
            return false;
        }

        if (!ageCompatible(profile1, profile2)) {
            return false;
        }

        return true;
    }

    private boolean hasCommonGameWithCommonServer(UserProfile profile1, UserProfile profile2) {
        if (profile1.getGames() == null || profile1.getGames().isEmpty() ||
            profile2.getGames() == null || profile2.getGames().isEmpty()) {
            return false;
        }

        Map<String, GameProfile> games1 = profile1.getGames().stream()
                .collect(Collectors.toMap(GameProfile::getGameName, gp -> gp));
        
        Map<String, GameProfile> games2 = profile2.getGames().stream()
                .collect(Collectors.toMap(GameProfile::getGameName, gp -> gp));

        Set<String> commonGames = new HashSet<>(games1.keySet());
        commonGames.retainAll(games2.keySet());

        if (commonGames.isEmpty()) {
            return false;
        }

        for (String game : commonGames) {
            Set<String> servers1 = games1.get(game).getPreferredServersSet();
            Set<String> servers2 = games2.get(game).getPreferredServersSet();

            if (servers1 == null || servers2 == null || servers1.isEmpty() || servers2.isEmpty()) {
                continue;
            }

            Set<String> commonServers = new HashSet<>(servers1);
            commonServers.retainAll(servers2);

            if (!commonServers.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private boolean withinPreferredDistance(UserProfile profile1, UserProfile profile2) {
        if (profile1.getLatitude() == null || profile1.getLongitude() == null ||
            profile2.getLatitude() == null || profile2.getLongitude() == null) {
            return false;
        }

        double distance = calculateHaversineDistance(
            profile1.getLatitude(), profile1.getLongitude(),
            profile2.getLatitude(), profile2.getLongitude()
        );

        Integer maxDist1 = profile1.getMaxPreferredDistance();
        Integer maxDist2 = profile2.getMaxPreferredDistance();

        return distance <= (maxDist1 != null ? maxDist1 : 200) &&
               distance <= (maxDist2 != null ? maxDist2 : 200);
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }

    private boolean ageCompatible(UserProfile profile1, UserProfile profile2) {
        Integer age1 = calculateAge(profile1.getBirthDate());
        Integer age2 = calculateAge(profile2.getBirthDate());

        if (age1 == null || age2 == null) {
            return false;
        }

        boolean user1AcceptsUser2 = isAgeInRange(age2, 
            profile1.getPreferredAgeMin(), profile1.getPreferredAgeMax());

        boolean user2AcceptsUser1 = isAgeInRange(age1,
            profile2.getPreferredAgeMin(), profile2.getPreferredAgeMax());

        return user1AcceptsUser2 && user2AcceptsUser1;
    }

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private boolean isAgeInRange(int age, Integer minAge, Integer maxAge) {
        int min = minAge != null ? minAge : 18;
        int max = maxAge != null ? maxAge : 100;
        return age >= min && age <= max;
    }

    private CompatibilityResult findCompatibleGamesWithScores(UserProfile profile1, UserProfile profile2, double threshold) {
        Map<String, GameProfile> games1 = profile1.getGames().stream()
                .collect(Collectors.toMap(GameProfile::getGameName, gp -> gp));
        
        Map<String, GameProfile> games2 = profile2.getGames().stream()
                .collect(Collectors.toMap(GameProfile::getGameName, gp -> gp));

        Set<String> commonGames = new HashSet<>(games1.keySet());
        commonGames.retainAll(games2.keySet());

        List<String> compatibleGames = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        for (String game : commonGames) {
            GameProfile g1 = games1.get(game);
            GameProfile g2 = games2.get(game);

            Set<String> servers1 = g1.getPreferredServersSet();
            Set<String> servers2 = g2.getPreferredServersSet();
            
            if (servers1 == null || servers2 == null || servers1.isEmpty() || servers2.isEmpty()) {
                continue;
            }

            Set<String> commonServers = new HashSet<>(servers1);
            commonServers.retainAll(servers2);

            if (commonServers.isEmpty()) {
                continue;
            }

            // Calculate total score: 40% baseline + up to 60% from compatibility
            double score = BASELINE_SCORE + calculateGameCompatibility(g1, g2, profile1, profile2);

            if (score >= threshold) {
                compatibleGames.add(game);
                scores.add(score);
            }
        }

        // Calculate average score across all compatible games
        double averageScore = scores.isEmpty() ? 0.0 : 
            scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return new CompatibilityResult(compatibleGames, averageScore);
    }

    /**
     * Calculate compatibility score from factors (0-60%)
     * This gets added to the 40% baseline
     */
    private double calculateGameCompatibility(GameProfile game1, GameProfile game2, UserProfile profile1, UserProfile profile2) {
        double totalScore = 0.0;

        // Game-specific factors (from GameProfile) - 12% + 9% = 21%
        totalScore += calculateExpLevelScore(game1.getExpLvl(), game2.getExpLvl());  // 12%
        totalScore += calculateGamingHoursScore(game1.getGamingHours(), game2.getGamingHours());  // 9%

        // User-wide factors (from UserProfile) - 15% + 3% + 9% + 12% = 39%
        totalScore += calculateCompetitivenessRankScore(
            profile1.getCompetitiveness(), game1.getCurrentRank(),
            profile2.getCompetitiveness(), game2.getCurrentRank()
        );  // 15%
        totalScore += calculateVoiceChatScore(profile1.getVoiceChatPreference(), profile2.getVoiceChatPreference());  // 3%
        totalScore += calculatePlayScheduleScore(profile1.getPlaySchedule(), profile2.getPlaySchedule());  // 9%
        totalScore += calculateMainGoalScore(profile1.getMainGoal(), profile2.getMainGoal());  // 12%

        return totalScore;
    }

    private double calculateExpLevelScore(String exp1, String exp2) {
        if (exp1 == null || exp2 == null) return 0.0;

        Map<String, Integer> expMap = Map.of(
            "Beginner", 1,
            "Intermediate", 2,
            "Advanced", 3
        );

        Integer e1 = expMap.get(exp1);
        Integer e2 = expMap.get(exp2);

        if (e1 == null || e2 == null) return 0.0;

        int diff = Math.abs(e1 - e2);

        if (diff == 0) return 12.0;
        if (diff == 1) return 6.0;
        return 0.0;
    }

    private double calculateGamingHoursScore(String hours1, String hours2) {
        if (hours1 == null || hours2 == null) return 0.0;
        return hours1.equals(hours2) ? 9.0 : 0.0;
    }

    private double calculateCompetitivenessRankScore(String comp1, String rank1, String comp2, String rank2) {
        if (comp1 == null || comp2 == null) return 0.0;

        if (!comp1.equals(comp2)) return 0.0;

        boolean bothCompetitive = (comp1.equals("Semi-competitive") || comp1.equals("Highly competitive"));

        if (!bothCompetitive) {
            return 15.0;
        }

        if (rank1 == null || rank2 == null || rank1.equals("N/A") || rank2.equals("N/A")) {
            return 0.0;
        }

        Map<String, Integer> rankMap = Map.of(
            "Unranked", 0,
            "Bronze", 1,
            "Silver", 2,
            "Gold", 3,
            "Platinum", 4,
            "Diamond", 5,
            "Master", 6,
            "Grandmaster", 7
        );

        Integer r1 = rankMap.get(rank1);
        Integer r2 = rankMap.get(rank2);

        if (r1 == null || r2 == null) return 0.0;

        int diff = Math.abs(r1 - r2);

        if (diff == 0) return 15.0;
        if (diff == 1) return 12.0;
        return 0.0;
    }

    private double calculateVoiceChatScore(String voice1, String voice2) {
        if (voice1 == null || voice2 == null) return 0.0;
        return voice1.equals(voice2) ? 3.0 : 0.0;
    }

    private double calculatePlayScheduleScore(String schedule1, String schedule2) {
        if (schedule1 == null || schedule2 == null) return 0.0;
        return schedule1.equals(schedule2) ? 9.0 : 0.0;
    }

    private double calculateMainGoalScore(String goal1, String goal2) {
        if (goal1 == null || goal2 == null) return 0.0;
        return goal1.equals(goal2) ? 12.0 : 0.0;
    }
}