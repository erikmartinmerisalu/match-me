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

            User user = userService.registerUser(
            authRequest.getEmail(),
            authRequest.getPassword()
        );

        String token = jwtUtil.generateToken(user.getEmail());
        
        // ADD COOKIE SETTING (same as login)
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(24*60*60)
            .build();

        AuthResponse responseBody = new AuthResponse(null, user.getId(), user.getEmail());
        
        return ResponseEntity.ok()
            .header("Set-Cookie", cookie.toString())
            .body(responseBody);

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
    public ResponseEntity<?> logout() {
        // Clear the JWT cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(0)  // Expire immediately
            .build();

        return ResponseEntity.ok()
            .header("Set-Cookie", cookie.toString())
            .body("Logged out successfully");
    }
    private boolean isValidEmail(String email) {

        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}


