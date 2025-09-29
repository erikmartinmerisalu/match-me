package com.matchme.controller;

import com.matchme.service.RecommendationService;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecommendationsController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<Long>> getRecommendations(@AuthenticationPrincipal String userEmail) {
        Optional<com.matchme.entity.User> userOpt = userService.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long userId = userOpt.get().getId();
        List<Long> recommendations = recommendationService.getRecommendations(userId, 10);
        return ResponseEntity.ok(recommendations);
    }
}