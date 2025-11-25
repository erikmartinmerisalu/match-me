package com.matchme.controller;

import com.matchme.service.RecommendationService;
import com.matchme.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"}, allowCredentials = "true")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<Long>> getRecommendations(HttpServletRequest request) {
        System.out.println("=== RECOMMENDATION REQUEST ===");
        
        String token = extractJwtFromCookie(request);
        
        if (token == null) {
            System.out.println("❌ No JWT token found in cookies");
            return ResponseEntity.status(401).body(null);
        }
        
        System.out.println("✅ Token found: " + token.substring(0, Math.min(20, token.length())) + "...");

        try {
            String userEmail = jwtUtil.extractUsername(token);
            
            if (userEmail == null) {
                System.out.println("❌ Could not extract email from token");
                return ResponseEntity.status(401).body(null);
            }
            
            System.out.println("✅ User email: " + userEmail);

            // Get top 10 recommendation IDs
            List<Long> recommendationIds = recommendationService.getTopRecommendationIds(userEmail, 10);
            
            System.out.println("✅ Found " + recommendationIds.size() + " recommendations");
            
            return ResponseEntity.ok(recommendationIds);
        } catch (Exception e) {
            System.out.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/compatible-games/{userId}")
    public ResponseEntity<List<String>> getCompatibleGames(@PathVariable Long userId, HttpServletRequest request) {
        System.out.println("=== COMPATIBLE GAMES REQUEST ===");
        
        String token = extractJwtFromCookie(request);
        
        if (token == null) {
            System.out.println("❌ No JWT token found in cookies");
            return ResponseEntity.status(401).body(null);
        }

        try {
            String currentUserEmail = jwtUtil.extractUsername(token);
            
            if (currentUserEmail == null) {
                System.out.println("❌ Could not extract email from token");
                return ResponseEntity.status(401).body(null);
            }
            
            System.out.println("✅ Getting compatible games between current user and user " + userId);

            // Get compatible games for this specific pairing
            List<String> compatibleGames = recommendationService.getCompatibleGamesForUsers(currentUserEmail, userId);
            
            System.out.println("✅ Found " + compatibleGames.size() + " compatible games");
            
            return ResponseEntity.ok(compatibleGames);
        } catch (Exception e) {
            System.out.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Helper method to extract JWT from cookie
    private String extractJwtFromCookie(HttpServletRequest request) {
        System.out.println("Checking for cookies...");
        
        if (request.getCookies() == null) {
            System.out.println("No cookies in request");
            return null;
        }
        
        System.out.println("Found " + request.getCookies().length + " cookies:");
        for (Cookie cookie : request.getCookies()) {
            System.out.println("  - Cookie: " + cookie.getName() + " = " + cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())) + "...");
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        
        System.out.println("No 'jwt' cookie found");
        return null;
    }
}