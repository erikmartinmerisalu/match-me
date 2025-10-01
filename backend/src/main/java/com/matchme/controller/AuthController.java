package com.matchme.controller;

import com.matchme.dto.AuthRequest;
import com.matchme.dto.AuthResponse;
import com.matchme.entity.User;
import com.matchme.service.UserService;
import com.matchme.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

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

            // Register user with only email & password
            User user = userService.registerUser(
                authRequest.getEmail(),
                authRequest.getPassword()
            );

            // JWT token generation


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
                //
            ResponseEntity<AuthResponse> response = ResponseEntity.ok()
                .header("Set-Cookie", 
                    ResponseCookie.from("jwt", token)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .sameSite("Lax")
                        .maxAge(24*60*60)
                        .build()
                        .toString()
                )
                .body(new AuthResponse(null, user.getId(), user.getEmail())); 

            return response;
            ///
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

        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}


