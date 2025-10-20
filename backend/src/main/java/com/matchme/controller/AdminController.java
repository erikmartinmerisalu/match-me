package com.matchme.controller;

import com.matchme.service.FakeUserSeederService; // ← Add this import
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final FakeUserSeederService fakeUserSeederService;

    @Autowired
    public AdminController(UserService userService, FakeUserSeederService fakeUserSeederService) {
        this.userService = userService;
        this.fakeUserSeederService = fakeUserSeederService;
    }

    @PostMapping("/create-fake-users")
    public ResponseEntity<String> createFakeUsers() {
        // Call the service that creates FULL profiles, not empty ones
        String result = fakeUserSeederService.seedFakeUsers(100);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete-fake-users")
    public ResponseEntity<String> deleteFakeUsers() {
        userService.deleteFakeUsers();
        return ResponseEntity.ok("All fake users deleted successfully!");
    }
}