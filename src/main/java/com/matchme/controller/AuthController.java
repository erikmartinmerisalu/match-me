package com.matchme.controller;

import com.matchme.dto.AuthRequest;
import com.matchme.dto.AuthResponse;
import com.matchme.entity.User;
import com.matchme.service.UserService;
import com.matchme.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest authRequest) {
        try {
            if (!isValidEmail(authRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Invalid email format");
            }
            if (authRequest.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body("Password must be at least 6 characters long");
            }

            // Create user WITH profile data to avoid validation issues
            User user = userService.registerUser(
                authRequest.getEmail(), 
                authRequest.getPassword(),
                authRequest.getDisplayName(),
                authRequest.getBirthDate()
            );

            String token = jwtUtil.generateToken(user.getEmail());
            AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Optional<User> userOpt = userService.authenticateUser(authRequest.getEmail(), authRequest.getPassword());
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String token = jwtUtil.generateToken(user.getEmail());
                
                AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid email or password");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // In a stateless JWT setup, logout is handled client-side by removing the token
        // We could implement a token blacklist here if needed
        return ResponseEntity.ok("Logged out successfully");
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}