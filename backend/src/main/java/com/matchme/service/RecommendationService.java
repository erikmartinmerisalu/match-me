package com.matchme.service;

import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (currentUserOpt.isEmpty() || currentUserOpt.get().getProfile() == null) return Collections.emptyList();

        User currentUser = currentUserOpt.get();
        UserProfile currentProfile = currentUser.getProfile();
        if (!currentProfile.isProfileCompleted()) return Collections.emptyList();

        List<UserProfile> allProfiles = userProfileRepository.findCompletedProfilesExcludingUser(userId);

        List<ScoredProfile> scoredProfiles = allProfiles.stream()
                .map(profile -> new ScoredProfile(profile, calculateCompatibilityScore(currentProfile, profile)))
                .filter(scored -> scored.score > 0.3)
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(maxResults)
                .collect(Collectors.toList());

        return scoredProfiles.stream()
                .map(scored -> scored.profile.getUser().getId())
                .collect(Collectors.toList());
    }

    private double calculateCompatibilityScore(UserProfile profile1, UserProfile profile2) {
        double score = 0.0, maxScore = 0.0;

        // 1. Server matching (25%)
        if (profile1.getPreferredServers() != null && profile2.getPreferredServers() != null) {
            Set<String> commonServers = new HashSet<>(profile1.getPreferredServers());
            commonServers.retainAll(profile2.getPreferredServers());
            score += commonServers.isEmpty() ? 0 : 0.25;
        }
        maxScore += 0.25;

        // 2. Game matching (25%)
        if (profile1.getGames() != null && profile2.getGames() != null) {
            Set<String> commonGames = new HashSet<>(profile1.getGames());
            commonGames.retainAll(profile2.getGames());
            score += commonGames.isEmpty() ? 0 : ((double) commonGames.size() / Math.max(profile1.getGames().size(), profile2.getGames().size())) * 0.25;
        }
        maxScore += 0.25;

        // 3. Gaming hours (20%) — now single string
        if (profile1.getGamingHours() != null && profile2.getGamingHours() != null) {
            score += profile1.getGamingHours().equals(profile2.getGamingHours()) ? 0.2 : 0;
        }
        maxScore += 0.2;

        // 4. Experience level (15%)
        if (profile1.getExpLvl() != null && profile2.getExpLvl() != null) {
            score += calculateExpLvlScore(profile1.getExpLvl(), profile2.getExpLvl()) * 0.15;
        }
        maxScore += 0.15;

        // 5. Rank (optional, 5%)
        if (profile1.getRank() != null && profile2.getRank() != null) {
            score += calculateRankCompatibility(profile1.getRank(), profile2.getRank()) * 0.05;
        }
        maxScore += 0.05;

        // 6. Age (15%)
        if (profile1.getAge() != null && profile2.getAge() != null) {
            int diff = Math.abs(profile1.getAge() - profile2.getAge());
            double ageScore = diff <= 3 ? 0.15 : Math.max(0, 0.15 - (diff - 3) * 0.03);
            score += ageScore;
        }
        maxScore += 0.15;

        // Region bonus
        if (profile1.getRegion() != null && profile1.getRegion().equals(profile2.getRegion())) {
            score += 0.1;
            maxScore += 0.1;
        }

        return maxScore > 0 ? score / maxScore : 0;
    }

    private double calculateRankCompatibility(String rank1, String rank2) {
        Map<String, Integer> rankTiers = Map.of(
            "Bronze", 1, "Silver", 2, "Gold", 3,
            "Platinum", 4, "Diamond", 5, "Master", 6, "Grandmaster", 7
        );
        Integer t1 = rankTiers.get(rank1), t2 = rankTiers.get(rank2);
        if (t1 == null || t2 == null) return 0.5;
        int diff = Math.abs(t1 - t2);
        if (diff <= 1) return 1.0;
        if (diff <= 2) return 0.7;
        if (diff <= 3) return 0.4;
        return 0.1;
    }

    private double calculateExpLvlScore(String lvl1, String lvl2) {
        Map<String, Integer> map = Map.of("Beginner",1,"Intermediate",2,"Advanced",3);
        Integer e1 = map.get(lvl1), e2 = map.get(lvl2);
        if (e1==null || e2==null) return 0.5;
        int diff = Math.abs(e1 - e2);
        if (diff==0) return 1.0;
        if (diff==1) return 0.7;
        return 0.3;
    }

    private static class ScoredProfile {
        UserProfile profile;
        double score;
        ScoredProfile(UserProfile profile, double score) { this.profile = profile; this.score = score; }
    }
}
