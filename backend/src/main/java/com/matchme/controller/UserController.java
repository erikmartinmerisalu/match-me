package com.matchme.controller;

import com.matchme.dto.GameProfileDto;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.GameProfile;
import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
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
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
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


            // @AuthenticationPrincipal String userEmail,
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

        profile.getGames().clear();
        if (profileDto.getGames() != null) {
            profileDto.getGames().forEach((gameName, gameDto) -> {
            GameProfile game = new GameProfile();
            game.setGameName(gameName);
            game.setExpLvl(gameDto.getExpLvl()); 
            game.setGamingHours(gameDto.getGamingHours());
            game.setPreferredServersSet(gameDto.getPreferredServers());
            game.setUserProfile(profile);
            profile.getGames().add(game);
        });
        }

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

        Map<String, GameProfileDto> gamesMap = new HashMap<>();
        profile.getGames().forEach(game -> {
            GameProfileDto g = new GameProfileDto();
            g.setExpLvl(game.getExpLvl()); 
            g.setPreferredServers(game.getPreferredServersSet());
            g.setGamingHours(game.getGamingHours());
            gamesMap.put(game.getGameName(), g);
        });
        dto.setGames(gamesMap);
        dto.setMaxPreferredDistance(profile.getMaxPreferredDistance());
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

