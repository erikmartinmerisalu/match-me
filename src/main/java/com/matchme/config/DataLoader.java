package com.matchme.config;

import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.service.UserService;
import com.matchme.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public void run(String... args) throws Exception {
        // Create test users if none exist
        if (userService.findByEmail("test@gamer.com").isEmpty()) {
            createTestUsers();
        }
    }

    private void createTestUsers() {
        // Create sample gamers
        createGamer("alice@gamer.com", "Alice", Set.of("US-East", "Europe"), 
                   Set.of("Valorant", "League of Legends"), Set.of("19:00-23:00"), 
                   "Diamond", LocalDate.of(1995, 5, 15), "NA", "EST");
        
        createGamer("bob@gamer.com", "Bob", Set.of("US-West", "US-East"), 
                   Set.of("CS:GO", "Valorant"), Set.of("20:00-02:00"), 
                   "Gold", LocalDate.of(1998, 8, 22), "NA", "PST");
        
        createGamer("charlie@gamer.com", "Charlie", Set.of("Europe"), 
                   Set.of("League of Legends", "Dota 2"), Set.of("18:00-22:00", "14:00-17:00"), 
                   "Platinum", LocalDate.of(1993, 3, 10), "EU", "CET");
    }

    private void createGamer(String email, String name, Set<String> servers, 
                            Set<String> games, Set<String> hours, String rank, 
                            LocalDate birthDate, String region, String timezone) {
        try {
            User user = userService.registerUser(email, "password123");
            UserProfile profile = user.getProfile();
            
            profile.setDisplayName(name);
            profile.setPreferredServers(servers);
            profile.setGames(games);
            profile.setGamingHours(hours);
            profile.setRank(rank);
            profile.setBirthDate(birthDate);
            profile.setRegion(region);
            profile.setTimezone(timezone);
            profile.setLookingFor("Competitive");
            profile.setPreferredAgeMin(20);
            profile.setPreferredAgeMax(30);
            
            userProfileService.saveProfile(profile);
        } catch (Exception e) {
            System.out.println("Failed to create user: " + email + " - " + e.getMessage());
        }
    }
}