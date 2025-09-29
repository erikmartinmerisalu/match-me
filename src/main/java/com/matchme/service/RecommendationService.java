// RecommendationService.java (new file)
package com.matchme.service;

import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserService userService;

    public List<Long> getRecommendations(Long userId, int maxResults) {
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
        
        // Score and sort profiles by compatibility
        List<ScoredProfile> scoredProfiles = allProfiles.stream()
                .map(profile -> new ScoredProfile(profile, calculateCompatibilityScore(currentProfile, profile)))
                .filter(scored -> scored.score > 0.3) // Only include reasonable matches
                .sorted((a, b) -> Double.compare(b.score, a.score)) // Descending order
                .limit(maxResults)
                .collect(Collectors.toList());

        return scoredProfiles.stream()
                .map(scored -> scored.profile.getUser().getId())
                .collect(Collectors.toList());
    }

    private double calculateCompatibilityScore(UserProfile profile1, UserProfile profile2) {
        double score = 0.0;
        double maxScore = 0.0;

        // 1. Server matching (25% weight)
        if (profile1.getPreferredServers() != null && profile2.getPreferredServers() != null) {
            Set<String> commonServers = new HashSet<>(profile1.getPreferredServers());
            commonServers.retainAll(profile2.getPreferredServers());
            double serverScore = commonServers.isEmpty() ? 0 : 0.25;
            score += serverScore;
        }
        maxScore += 0.25;

        // 2. Game matching (25% weight)
        if (profile1.getGames() != null && profile2.getGames() != null) {
            Set<String> commonGames = new HashSet<>(profile1.getGames());
            commonGames.retainAll(profile2.getGames());
            double gameScore = commonGames.isEmpty() ? 0 : (double) commonGames.size() / 
                Math.max(profile1.getGames().size(), profile2.getGames().size()) * 0.25;
            score += gameScore;
        }
        maxScore += 0.25;

        // 3. Gaming hours matching (20% weight)
        if (profile1.getGamingHours() != null && profile2.getGamingHours() != null) {
            Set<String> commonHours = new HashSet<>(profile1.getGamingHours());
            commonHours.retainAll(profile2.getGamingHours());
            double hoursScore = commonHours.isEmpty() ? 0 : 0.2;
            score += hoursScore;
        }
        maxScore += 0.2;

        // 4. Rank compatibility (15% weight)
        if (profile1.getRank() != null && profile2.getRank() != null) {
            double rankScore = calculateRankCompatibility(profile1.getRank(), profile2.getRank());
            score += rankScore * 0.15;
        }
        maxScore += 0.15;

        // 5. Age compatibility (15% weight) - ±3 year gap
        if (profile1.getAge() != null && profile2.getAge() != null) {
            int ageDiff = Math.abs(profile1.getAge() - profile2.getAge());
            double ageScore = ageDiff <= 3 ? 0.15 : Math.max(0, 0.15 - (ageDiff - 3) * 0.03);
            score += ageScore;
        }
        maxScore += 0.15;

        // Region bonus
        if (profile1.getRegion() != null && profile2.getRegion() != null && 
            profile1.getRegion().equals(profile2.getRegion())) {
            score += 0.1;
            maxScore += 0.1;
        }

        return maxScore > 0 ? score / maxScore : 0;
    }

    private double calculateRankCompatibility(String rank1, String rank2) {
        // Define rank tiers for compatibility
        Map<String, Integer> rankTiers = Map.of(
            "Bronze", 1, "Silver", 2, "Gold", 3, 
            "Platinum", 4, "Diamond", 5, "Master", 6, "Grandmaster", 7
        );

        Integer tier1 = rankTiers.get(rank1);
        Integer tier2 = rankTiers.get(rank2);

        if (tier1 == null || tier2 == null) {
            return 0.5; // Neutral score for unknown ranks
        }

        int diff = Math.abs(tier1 - tier2);
        if (diff <= 1) return 1.0;    // Same or adjacent tier
        if (diff <= 2) return 0.7;    // One tier apart
        if (diff <= 3) return 0.4;    // Two tiers apart
        return 0.1;                   // More than two tiers apart
    }

    private static class ScoredProfile {
        UserProfile profile;
        double score;

        ScoredProfile(UserProfile profile, double score) {
            this.profile = profile;
            this.score = score;
        }
    }
}