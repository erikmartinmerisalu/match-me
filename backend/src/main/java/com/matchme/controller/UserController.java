package com.matchme.controller;

import com.matchme.dto.GameProfileDto;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.Connection;
import com.matchme.entity.GameProfile;
import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.repository.ConnectionRepository;
import com.matchme.service.UserProfileService;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"}, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ConnectionRepository connectionRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id, @AuthenticationPrincipal String userEmail) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if (!canViewProfile(userEmail, user)) {
            return ResponseEntity.notFound().build();
        }

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setDisplayName(user.getProfile().getDisplayName());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id, @AuthenticationPrincipal String userEmail) {

        UserProfile profile = userProfileService.findByUserId(id);

        if (!canViewProfile(userEmail, profile.getUser())) {
            return ResponseEntity.notFound().build();
        }

        UserProfileDto dto = mapToProfileDto(profile);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/bio")
    public ResponseEntity<?> getUserBio(@PathVariable Long id, @AuthenticationPrincipal String userEmail) {

        UserProfile profile = userProfileService.findByUserId(id);

        if (!canViewProfile(userEmail, profile.getUser())) {
            return ResponseEntity.notFound().build();
        }

        UserProfileDto dto = mapToProfileDto(profile);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal String userEmail) {
        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setDisplayName(user.getProfile().getDisplayName());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getCurrentUserProfile(@AuthenticationPrincipal String userEmail) {
        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserProfileDto dto = mapToProfileDto(user.getProfile());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/bio")
    public ResponseEntity<?> getCurrentUserBio(@AuthenticationPrincipal String userEmail) {
        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserProfile profile = userOpt.get().getProfile();

        UserProfileDto dto = mapToProfileDto(profile);
        return ResponseEntity.ok(dto);

    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @Valid @RequestBody UserProfileDto profileDto) {

            String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserProfile profile = user.getProfile();

        profile.setDisplayName(profileDto.getDisplayName());
        profile.setAboutMe(profileDto.getAboutMe());

        profile.setBirthDate(profileDto.getBirthDate());
        profile.setTimezone(profileDto.getTimezone());

        profile.setLookingFor(profileDto.getLookingFor());
        profile.setPreferredAgeMin(profileDto.getPreferredAgeMin());
        profile.setPreferredAgeMax(profileDto.getPreferredAgeMax());
        profile.setMaxPreferredDistance(profileDto.getMaxPreferredDistance());
        profile.setProfilePic(profileDto.getProfilePic());
        profile.setLatitude(profileDto.getLatitude());
        profile.setLongitude(profileDto.getLongitude());
        profile.setLocation(profileDto.getLocation());

        profile.getGames().clear();
        if (profileDto.getGames() != null) {
            profileDto.getGames().forEach((gameName, gameDto) -> {
                GameProfile game = new GameProfile();
                game.setGameName(gameName);
                game.setExpLvl(gameDto.getExpLvl()); 
                game.setGamingHours(gameDto.getGamingHours());
                game.setPreferredServersSet(gameDto.getPreferredServers());
                
                // NEW FIELDS
                game.setCompetitiveness(gameDto.getCompetitiveness());
                game.setVoiceChatPreference(gameDto.getVoiceChatPreference());
                game.setPlaySchedule(gameDto.getPlaySchedule());
                game.setMainGoal(gameDto.getMainGoal());
                game.setCurrentRank(gameDto.getCurrentRank());
                
                game.setUserProfile(profile);
                profile.getGames().add(game);
            });
        }

        UserProfile savedProfile = userProfileService.saveProfile(profile);
        UserProfileDto responseDto = mapToProfileDto(savedProfile);

        return ResponseEntity.ok(responseDto);
    }

    private boolean canViewProfile(String currentUserEmail, User targetUser) {
        // Can always view own profile
        if (targetUser.getEmail().equals(currentUserEmail)) {
            return true;
        }
        
        // Check if users are connected (for chat feature)
        Optional<User> currentUserOpt = userService.findByEmail(currentUserEmail);
        if (currentUserOpt.isPresent()) {
            Long currentUserId = currentUserOpt.get().getId();
            Optional<Connection> connection = connectionRepository.findConnectionBetweenUsers(currentUserId, targetUser.getId());
            if (connection.isPresent() && connection.get().getStatus() == Connection.ConnectionStatus.ACCEPTED) {
                return true;
            }
        }
        
        return false;
    }

    private UserProfileDto mapToProfileDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();

        dto.setId(profile.getUser().getId());
        dto.setDisplayName(profile.getDisplayName());
        dto.setAboutMe(profile.getAboutMe());

        Map<String, GameProfileDto> gamesMap = new HashMap<>();
        profile.getGames().forEach(game -> {
            GameProfileDto g = new GameProfileDto();
            g.setExpLvl(game.getExpLvl()); 
            g.setPreferredServers(game.getPreferredServersSet());
            g.setGamingHours(game.getGamingHours());
            
            // NEW FIELDS
            g.setCompetitiveness(game.getCompetitiveness());
            g.setVoiceChatPreference(game.getVoiceChatPreference());
            g.setPlaySchedule(game.getPlaySchedule());
            g.setMainGoal(game.getMainGoal());
            g.setCurrentRank(game.getCurrentRank());
            
            gamesMap.put(game.getGameName(), g);
        });
        dto.setGames(gamesMap);
        dto.setMaxPreferredDistance(profile.getMaxPreferredDistance());
        dto.setBirthDate(profile.getBirthDate());
        dto.setTimezone(profile.getTimezone());
        dto.setLookingFor(profile.getLookingFor());
        dto.setPreferredAgeMin(profile.getPreferredAgeMin());
        dto.setPreferredAgeMax(profile.getPreferredAgeMax());
        dto.setProfileCompleted(profile.isProfileCompleted());
        dto.setProfilePic(profile.getProfilePic());
        dto.setLatitude(profile.getLatitude());
        dto.setLongitude(profile.getLongitude());
        dto.setLocation(profile.getLocation());

        return dto;
    }
}