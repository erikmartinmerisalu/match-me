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
    public ResponseEntity<?> updateCurrentUserProfile( @Valid @RequestBody UserProfileDto dto ) {

    String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        Long userId = userOpt.get().getId(); // ← SIIT SAAD USER_ID
         try {
            UserProfile updatedProfile = userProfileService.updateCurrentUserProfile(userId, dto);
            return ResponseEntity.ok(mapToProfileDto(updatedProfile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

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
}