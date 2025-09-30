package com.matchme.controller;

import com.matchme.entity.User;
import com.matchme.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/connections")
public class ConnectionsController {

    private final UserRepository userRepo;

    // In-memory maps just for demo. Replace with proper entity (Connection) + JPA repo.
    private final Map<Long, Set<Long>> pendingRequests = new HashMap<>();
    private final Map<Long, Set<Long>> acceptedConnections = new HashMap<>();

    public ConnectionsController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Request connection
    @PostMapping("/{id}")
    public ResponseEntity<?> requestConnection(@PathVariable Long id, @RequestHeader("X-User-Id") Long meId) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (Objects.equals(meId, id)) {
            return ResponseEntity.badRequest().body("Cannot connect to yourself");
        }
        pendingRequests.computeIfAbsent(id, k -> new HashSet<>()).add(meId);
        return ResponseEntity.ok("Connection request sent to user " + id);
    }

    // Accept connection
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptConnection(@PathVariable Long id, @RequestHeader("X-User-Id") Long meId) {
        var requests = pendingRequests.getOrDefault(meId, Set.of());
        if (!requests.contains(id)) {
            return ResponseEntity.badRequest().body("No pending request from user " + id);
        }
        // remove from pending
        pendingRequests.get(meId).remove(id);
        // add to accepted for both sides
        acceptedConnections.computeIfAbsent(meId, k -> new HashSet<>()).add(id);
        acceptedConnections.computeIfAbsent(id, k -> new HashSet<>()).add(meId);
        return ResponseEntity.ok("Connection accepted with user " + id);
    }

    // List connections
    @GetMapping
    public ResponseEntity<?> listConnections(@RequestHeader("X-User-Id") Long meId) {
        var connections = acceptedConnections.getOrDefault(meId, Set.of());
        return ResponseEntity.ok(connections);
    }

    // Disconnect
    @DeleteMapping("/{id}")
    public ResponseEntity<?> disconnect(@PathVariable Long id, @RequestHeader("X-User-Id") Long meId) {
        acceptedConnections.getOrDefault(meId, new HashSet<>()).remove(id);
        acceptedConnections.getOrDefault(id, new HashSet<>()).remove(meId);
        return ResponseEntity.ok("Disconnected from user " + id);
    }
}
