package com.matchme.controller;

import com.matchme.config.MatchWebSocketHandler; // ADDED
import com.matchme.dto.ConnectionDto;
import com.matchme.entity.Connection;
import com.matchme.entity.User;
import com.matchme.repository.ConnectionRepository;
import com.matchme.service.ConnectionService;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/connections")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"}, allowCredentials = "true")
public class ConnectionsController {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private MatchWebSocketHandler matchWebSocketHandler; // ADDED: For WebSocket broadcasting

    // Helper method for WebSocket broadcasting
    private void broadcastMatchUpdate(Long userId1, Long userId2) {
        System.out.println("Broadcasting match update to users: " + userId1 + " and " + userId2);
        
        // Notify both users about the match update
        try {
            matchWebSocketHandler.sendMatchUpdateToUser(userId1);
            matchWebSocketHandler.sendMatchUpdateToUser(userId2);
        } catch (Exception e) {
            System.err.println("Error broadcasting match update: " + e.getMessage());
        }
    }

    // Get all accepted connections
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getConnections(@AuthenticationPrincipal User currentUser) {
        var connections = connectionRepository.findAcceptedConnectionsForUser(currentUser.getId());
        var dtos = connections.stream()
                .map(ConnectionDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get pending requests received
    @GetMapping("/pending/received")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPendingRequestsReceived(@AuthenticationPrincipal User currentUser) {
        var connections = connectionRepository.findPendingConnectionsForUser(currentUser.getId());
        var dtos = connections.stream()
                .map(ConnectionDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get pending requests sent
    @GetMapping("/pending/sent")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPendingRequestsSent(@AuthenticationPrincipal User currentUser) {
        var connections = connectionRepository.findByFromUserIdAndStatus(
            currentUser.getId(), 
            Connection.ConnectionStatus.PENDING
        );
        var dtos = connections.stream()
                .map(ConnectionDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Send match request - UPDATED: Uses safe service method
   @PostMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> sendMatchRequest(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot connect to yourself"));
        }

        Optional<User> targetUserOpt = userService.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Use safe service method to prevent duplicates
        ConnectionService.ConnectionCreationResult result = connectionService.findOrCreateConnection(
            currentUser.getId(), 
            userId, 
            Connection.ConnectionStatus.PENDING
        );

        // If the connection already existed (was found, not created), return error
        if (!result.isCreated()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Connection already exists"));
        }

        // ADDED: Broadcast WebSocket update to both users
        broadcastMatchUpdate(currentUser.getId(), userId);

        return ResponseEntity.ok(Map.of("message", "Match request sent successfully"));
    }

    //dismiss user
    @PostMapping("/{userId}/dismiss")
    @Transactional
    public ResponseEntity<?> dismissUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot dismiss yourself"));
        }

        Optional<User> targetUserOpt = userService.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Use safe service method to prevent duplicates
        ConnectionService.ConnectionCreationResult result = connectionService.findOrCreateConnection(
            currentUser.getId(), 
            userId, 
            Connection.ConnectionStatus.DISMISSED
        );

        // If the connection already existed (was found, not created), return error
        if (!result.isCreated()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Connection already exists"));
        }

        return ResponseEntity.ok(Map.of("message", "User dismissed successfully"));
    }

    // Accept match request
    @PostMapping("/{connectionId}/accept")
    @Transactional
    public ResponseEntity<?> acceptMatchRequest(
            @PathVariable Long connectionId,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Connection> connectionOpt = connectionRepository.findById(connectionId);
        if (connectionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Connection connection = connectionOpt.get();

        if (!connection.getToUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Not authorized"));
        }

        if (connection.getStatus() != Connection.ConnectionStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Connection is not pending"));
        }

        connection.setStatus(Connection.ConnectionStatus.ACCEPTED);
        connectionRepository.save(connection);

        // ADDED: Broadcast WebSocket update to both users
        broadcastMatchUpdate(connection.getFromUser().getId(), connection.getToUser().getId());

        return ResponseEntity.ok(Map.of("message", "Match request accepted"));
    }

    // Reject match request
    @PostMapping("/{connectionId}/reject")
    @Transactional
    public ResponseEntity<?> rejectMatchRequest(
            @PathVariable Long connectionId,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Connection> connectionOpt = connectionRepository.findById(connectionId);
        if (connectionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Connection connection = connectionOpt.get();

        if (!connection.getToUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Not authorized"));
        }

        if (connection.getStatus() != Connection.ConnectionStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Connection is not pending"));
        }

        connection.setStatus(Connection.ConnectionStatus.REJECTED);
        connectionRepository.save(connection);

        // ADDED: Broadcast WebSocket update to both users
        broadcastMatchUpdate(connection.getFromUser().getId(), connection.getToUser().getId());

        return ResponseEntity.ok(Map.of("message", "Match request rejected"));
    }

    // Unmatch
    @DeleteMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> unmatch(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Connection> connectionOpt = connectionRepository
                .findConnectionBetweenUsers(currentUser.getId(), userId);
        
        if (connectionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Connection connection = connectionOpt.get();

        if (connection.getStatus() != Connection.ConnectionStatus.ACCEPTED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Can only unmatch accepted connections"));
        }

        connectionRepository.delete(connection);

        // ADDED: Broadcast WebSocket update to both users
        broadcastMatchUpdate(currentUser.getId(), userId);

        return ResponseEntity.ok(Map.of("message", "Unmatched successfully"));
    }

    // Block a user
    @PostMapping("/{userId}/block")
    @Transactional
    public ResponseEntity<?> blockUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {

        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot block yourself"));
        }

        Optional<User> targetUserOpt = userService.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User currentUserEntity = userService.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        User toUser = targetUserOpt.get();

        // Check if blocking already exists where current user is the blocker
        Optional<Connection> existingBlock = connectionRepository
                .findBlockedConnectionBetweenUsers(currentUser.getId(), userId);

        if (existingBlock.isPresent()) {
            // We already have a BLOCKED connection for this pair (any direction) — ensure direction is correct
            Connection conn = existingBlock.get();
            // Force correct direction — important if the existing connection was stored in reversed direction
            conn.setFromUser(currentUserEntity);
            conn.setToUser(toUser);
            conn.setStatus(Connection.ConnectionStatus.BLOCKED);
            connectionRepository.save(conn);

            broadcastMatchUpdate(currentUser.getId(), userId);
            return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
        }

        // No blocked connection found — check if any connection exists (any status)
        Optional<Connection> existingConnection = connectionRepository
                .findConnectionBetweenUsers(currentUser.getId(), userId);

        Connection connection;
        if (existingConnection.isPresent()) {
            // Reuse the existing connection but force direction to currentUser -> targetUser
            connection = existingConnection.get();
            connection.setFromUser(currentUserEntity);
            connection.setToUser(toUser);
            connection.setStatus(Connection.ConnectionStatus.BLOCKED);
        } else {
            // Create new BLOCKED connection
            connection = new Connection(currentUserEntity, toUser, Connection.ConnectionStatus.BLOCKED);
        }

        connectionRepository.save(connection);

        // Broadcast
        broadcastMatchUpdate(currentUser.getId(), userId);

        return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
    }

    // Unblock a user
    @PostMapping("/{userId}/unblock")
    @Transactional
    public ResponseEntity<?> unblockUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Connection> blockedConnection = connectionRepository
                .findBlockedConnectionBetweenUsers(currentUser.getId(), userId);
        
        if (blockedConnection.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No blocked connection found with this user"));
        }

        // Delete the blocked connection (or set to DISMISSED if you want to keep history)
        connectionRepository.delete(blockedConnection.get());

        // ADDED: Broadcast WebSocket update to both users
        broadcastMatchUpdate(currentUser.getId(), userId);

        return ResponseEntity.ok(Map.of("message", "User unblocked successfully"));
    }

    // Get list of users blocked by current user - CORRECTED
    @GetMapping("/blocked")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getBlockedUsers(@AuthenticationPrincipal User currentUser) {
        System.out.println("Fetching users blocked by user ID: " + currentUser.getId());
        
        // This should return connections where current user is the FROM user (the blocker)
        List<Connection> blockedConnections = connectionRepository.findUsersBlockedByUser(currentUser.getId());
        
        System.out.println("Found " + blockedConnections.size() + " blocked connections where current user is the blocker");
        
        // Create a list with user details - the blocked user is the TO user
        List<Map<String, Object>> blockedUsersWithDetails = blockedConnections.stream()
                .map(connection -> {
                    // The blocked user is the toUser (the one who was blocked)
                    User blockedUser = connection.getToUser();
                    System.out.println("Blocked user - From: " + connection.getFromUser().getId() + 
                                    " (blocker), To: " + blockedUser.getId() + " (blocked)");
                    
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", connection.getId()); // Connection ID for unblocking
                    userInfo.put("userId", blockedUser.getId()); // User ID of the blocked user
                    userInfo.put("displayName", blockedUser.getProfile().getDisplayName());
                    userInfo.put("profilePic", blockedUser.getProfile().getProfilePic());
                    userInfo.put("status", "blocked");
                    return userInfo;
                })
                .collect(Collectors.toList());
        
        System.out.println("Returning " + blockedUsersWithDetails.size() + " users blocked by current user");
        
        return ResponseEntity.ok(blockedUsersWithDetails);
    }
}