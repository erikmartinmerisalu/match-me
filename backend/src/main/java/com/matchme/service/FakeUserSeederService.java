package com.matchme.service;

import com.matchme.entity.GameProfile;
import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class FakeUserSeederService {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final PasswordEncoder passwordEncoder;

    public FakeUserSeederService(UserService userService, UserProfileService userProfileService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.passwordEncoder = passwordEncoder;
    }

    public String seedFakeUsers(int count) {
    String[] serverOptions = {"N-America", "S-America", "EU East", "EU West", "Asia", "AU+SEA", "Africa+Middle east"};
    String[] gameOptions = {"Game1", "Game2", "Game3", "Game4", "Game5"};
    String[] gameExpLvl = {"Beginner", "Intermediate", "Advanced"};
    String[] gamingHours = {"<100", "101-500", "501-1000", "1000+"};
    String[] competitivenessOptions = {"Just for fun", "Casual", "Semi-competitive", "Highly competitive"};
    String[] voiceChatOptions = {"Always", "Sometimes", "Rarely", "Never"};
    String[] playScheduleOptions = {"Weekday mornings", "Weekday evenings", "Weekend mornings", "Weekend evenings", "Late nights"};
    String[] mainGoalOptions = {"Rank climbing", "Learning", "Making friends", "Casual fun"};
    String[] rankOptions = {"Unranked", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "N/A"};

    Random random = new Random();
    int successCount = 0;

    for (int i = 1; i <= count; i++) {
        try {
            String email = "fakeuser" + i + "@example.com";
            if (userService.findByEmail(email).isPresent()) continue;

            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("TestPass123"));
            user.setIsFake(true); 
            user = userService.save(user);

            UserProfile profile = new UserProfile();
            profile.setUser(user);
            profile.setDisplayName("Player" + i);
            profile.setAboutMe("Hi, I'm Player" + i + " and I love gaming!");
            profile.setBirthDate(LocalDate.of(1985 + random.nextInt(20), 1 + random.nextInt(12), 1 + random.nextInt(28)));
            profile.setTimezone("UTC" + (random.nextInt(5) - 2));
            profile.setLookingFor("Friends and teammates");
            profile.setPreferredAgeMin(18 + random.nextInt(5));
            profile.setPreferredAgeMax(25 + random.nextInt(10));
            profile.setMaxPreferredDistance(50 + random.nextInt(150));
            profile.setProfilePic("https://api.dicebear.com/7.x/thumbs/svg?seed=" + i);
            profile.setLatitude(-90 + random.nextDouble() * 180);
            profile.setLongitude(-180 + random.nextDouble() * 360);
            profile.setLocation("Somewhere #" + i);

            int gameCount = 1 + random.nextInt(3);
            Set<GameProfile> games = new HashSet<>();
            List<String> gameList = new ArrayList<>(Arrays.asList(gameOptions));
            Collections.shuffle(gameList);

            for (int j = 0; j < gameCount; j++) {
                String gameName = gameList.get(j);
                GameProfile game = new GameProfile();
                game.setGameName(gameName);
                game.setExpLvl(gameExpLvl[random.nextInt(gameExpLvl.length)]);
                game.setGamingHours(gamingHours[random.nextInt(gamingHours.length)]);
                game.setPreferredServersSet(Set.of(serverOptions[random.nextInt(serverOptions.length)]));
                game.setCompetitiveness(competitivenessOptions[random.nextInt(competitivenessOptions.length)]);
                game.setVoiceChatPreference(voiceChatOptions[random.nextInt(voiceChatOptions.length)]);
                game.setPlaySchedule(playScheduleOptions[random.nextInt(playScheduleOptions.length)]);
                game.setMainGoal(mainGoalOptions[random.nextInt(mainGoalOptions.length)]);
                game.setCurrentRank(rankOptions[random.nextInt(rankOptions.length)]);
                game.setUserProfile(profile);
                games.add(game);
            }

            profile.setGames(new ArrayList<>(games));
            profile.setProfileCompleted(true);
            
            // Try to save and catch any validation errors
            userProfileService.saveProfile(profile);
            successCount++;
            
        } catch (Exception e) {
            System.err.println("Failed to create fake user " + i + ": " + e.getMessage());
            // Continue with next user instead of stopping
        }
    }

    return "✅ Successfully seeded " + successCount + " out of " + count + " fictitious users.";
    }
}