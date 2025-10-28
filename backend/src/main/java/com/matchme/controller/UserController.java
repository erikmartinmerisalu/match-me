package com.matchme.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getUser(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if (!canViewProfile(currentUser, user)) {
            return ResponseEntity.status(403).build();
        }

        UserProfileDto dto = mapToProfileDto(user.getProfile());


        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        UserProfile profile = userProfileService.findByUserId(id);
        if (!canViewProfile(currentUser, profile.getUser())) {
            return ResponseEntity.status(403).build();
        }

        UserProfileDto dto = mapToProfileDto(profile);
        return ResponseEntity.ok(dto);
    }
    

    @GetMapping("/{id}/bio")
    public ResponseEntity<?> getUserBio(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        UserProfile profile = userProfileService.findByUserId(id);
        if (!canViewProfile(currentUser, profile.getUser())) {
            return ResponseEntity.status(403).build();
        }

        UserProfileDto dto = mapToBioDto(profile);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserProfileDto dto = mapToUserDto(currentUser.getProfile());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getCurrentUserProfile(@AuthenticationPrincipal User currentUser) {
        UserProfileDto dto = mapToProfileDto(currentUser.getProfile());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/bio")
    public ResponseEntity<?> getCurrentUserBio(@AuthenticationPrincipal User currentUser) {
        UserProfile profile = currentUser.getProfile();
        UserProfileDto dto = mapToBioDto(profile);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
        @AuthenticationPrincipal User currentUser,
        @RequestBody JsonNode json) {

        Long userId = currentUser.getId();

        try {
            userProfileService.updateProfile(userId, json);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    private boolean canViewProfile(User currentUser, User targetUser) {
        // 1. Own profile
        if (targetUser.getId().equals(currentUser.getId())) {
            return true;
        }
        
        // Check for existing connection
        Optional<Connection> connectionOpt = connectionRepository
                .findConnectionBetweenUsers(currentUser.getId(), targetUser.getId());
        
        if (connectionOpt.isPresent()) {
            Connection connection = connectionOpt.get();
            Connection.ConnectionStatus status = connection.getStatus();
            
            // 2. Connected (ACCEPTED)
            if (status == Connection.ConnectionStatus.ACCEPTED) {
                return true;
            }
            
            // 3. Outstanding connection request (PENDING)
            if (status == Connection.ConnectionStatus.PENDING) {
                return true;
            }
            
            // REJECTED, DISMISSED, BLOCKED = cannot view
            return false;
        }
        
        
        return true;
    }

    private UserProfileDto mapToProfileDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();

        dto.setId(profile.getUser().getId());
        dto.setAboutMe(profile.getAboutMe());
        dto.setLookingFor(profile.getLookingFor());

        return dto;
    }

    private UserProfileDto mapToBioDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();

        dto.setId(profile.getUser().getId());
        dto.setMaxPreferredDistance(profile.getMaxPreferredDistance());
        dto.setBirthDate(profile.getBirthDate());
        dto.setPreferredAgeMin(profile.getPreferredAgeMin());
        dto.setPreferredAgeMax(profile.getPreferredAgeMax());
        dto.setLatitude(profile.getLatitude());
        dto.setLongitude(profile.getLongitude());
        dto.setLocation(profile.getLocation());
        dto.setCompetitiveness(profile.getCompetitiveness());
        dto.setVoiceChatPreference(profile.getVoiceChatPreference());
        dto.setPlaySchedule(profile.getPlaySchedule());
        dto.setMainGoal(profile.getMainGoal());

        dto.setGames(new HashMap<>());
        if (profile.getGames() != null) {
            profile.getGames().forEach(game -> {
                dto.getGames().put(game.getGameName(), new GameProfileDto(
                        game.getPreferredServersSet(),
                        game.getExpLvl(),
                        game.getGamingHours(),
                        game.getCurrentRank()
                ));
            });
        }

        return dto;
    }

        private UserProfileDto mapToUserDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();

        dto.setId(profile.getUser().getId());
        dto.setDisplayName(profile.getDisplayName());
        dto.setProfileCompleted(profile.isProfileCompleted());
        dto.setProfilePic(profile.getProfilePic());

        return dto;
    }
}