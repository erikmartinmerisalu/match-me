package com.matchme.controller;

import com.matchme.dto.UserProfileDto;
import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.service.UserProfileService;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

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


        // Return only the biographical data used for recommendations

        UserProfileDto dto = new UserProfileDto();
        dto.setId(profile.getUser().getId());
        dto.setPreferredServers(profile.getPreferredServers());
        dto.setGames(profile.getGames());
        dto.setGamingHours(profile.getGamingHours());
        dto.setRank(profile.getRank());
        dto.setBirthDate(profile.getBirthDate());
        dto.setAge(profile.getAge());
        dto.setTimezone(profile.getTimezone());

        dto.setRegion(profile.getRegion());

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


        UserProfileDto dto = new UserProfileDto();
        dto.setId(profile.getUser().getId());
        dto.setPreferredServers(profile.getPreferredServers());
        dto.setGames(profile.getGames());
        dto.setGamingHours(profile.getGamingHours());
        dto.setRank(profile.getRank());
        dto.setBirthDate(profile.getBirthDate());
        dto.setAge(profile.getAge());
        dto.setTimezone(profile.getTimezone());



        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @AuthenticationPrincipal String userEmail,
            @Valid @RequestBody UserProfileDto profileDto) {

        Optional<User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserProfile profile = user.getProfile();

        profile.setDisplayName(profileDto.getDisplayName());
        profile.setAboutMe(profileDto.getAboutMe());
        profile.setPreferredServers(profileDto.getPreferredServers());
        profile.setGames(profileDto.getGames());
        profile.setGamingHours(profileDto.getGamingHours());
        profile.setRank(profileDto.getRank());
        profile.setBirthDate(profileDto.getBirthDate());
        profile.setTimezone(profileDto.getTimezone());

        profile.setLookingFor(profileDto.getLookingFor());
        profile.setPreferredAgeMin(profileDto.getPreferredAgeMin());
        profile.setPreferredAgeMax(profileDto.getPreferredAgeMax());

        UserProfile savedProfile = userProfileService.saveProfile(profile);
        UserProfileDto responseDto = mapToProfileDto(savedProfile);

        return ResponseEntity.ok(responseDto);
    }

    private boolean canViewProfile(String currentUserEmail, User targetUser) {

        if (targetUser.getEmail().equals(currentUserEmail)) {
            return true;
        }
        // Only allow viewing own profile for now
        return false;
    }


    private UserProfileDto mapToProfileDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(profile.getUser().getId());
        dto.setDisplayName(profile.getDisplayName());
        dto.setAboutMe(profile.getAboutMe());
        dto.setPreferredServers(profile.getPreferredServers());
        dto.setGames(profile.getGames());
        dto.setGamingHours(profile.getGamingHours());
        dto.setRank(profile.getRank());
        dto.setBirthDate(profile.getBirthDate());
        dto.setAge(profile.getAge());
        dto.setTimezone(profile.getTimezone());
        dto.setLookingFor(profile.getLookingFor());
        dto.setPreferredAgeMin(profile.getPreferredAgeMin());
        dto.setPreferredAgeMax(profile.getPreferredAgeMax());
        dto.setProfileCompleted(profile.isProfileCompleted());

        return dto;
    }
}

