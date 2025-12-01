package com.matchme.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.matchme.dto.UserProfileDto;
import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.service.UserProfileService;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"}, allowCredentials = "true")
public class MeController {

    private final UserController userController;

    @Autowired
    public MeController(UserController userController) {
        this.userController = userController;
    }

    // GET /api/me
    @GetMapping
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return userController.getCurrentUser(currentUser);
    }

    // GET /api/me/profile
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(name = "games", required = false) String gamesParam) {
        return userController.getCurrentUserProfile(currentUser, gamesParam);
    }

    // GET /api/me/bio
    @GetMapping("/bio")
    public ResponseEntity<?> getCurrentUserBio(@AuthenticationPrincipal User currentUser) {
        return userController.getCurrentUserBio(currentUser);
    }

    // PUT /api/me/profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody JsonNode json) {
        return userController.updateCurrentUserProfile(currentUser, json);
    }
}
