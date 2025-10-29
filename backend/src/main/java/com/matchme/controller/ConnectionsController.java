package com.matchme.controller;

import com.matchme.dto.ConnectionDto;
import com.matchme.entity.Connection;
import com.matchme.entity.User;
import com.matchme.repository.ConnectionRepository;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    // Send match request
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
        User targetUser = targetUserOpt.get();

        Optional<Connection> existingConnection = connectionRepository
                .findConnectionBetweenUsers(currentUser.getId(), userId);
        
        if (existingConnection.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Connection already exists"));
        }

        Connection connection = new Connection(
                currentUser,
                targetUser,
                Connection.ConnectionStatus.PENDING
        );

        connectionRepository.save(connection);

        return ResponseEntity.ok(Map.of("message", "Match request sent successfully"));
    }

    // Dismiss user
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
        User targetUser = targetUserOpt.get();

        Optional<Connection> existingConnection = connectionRepository
                .findConnectionBetweenUsers(currentUser.getId(), userId);
        
        if (existingConnection.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Connection already exists"));
        }

        Connection connection = new Connection(
                currentUser,
                targetUser,
                Connection.ConnectionStatus.DISMISSED
        );

        connectionRepository.save(connection);

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

        return ResponseEntity.ok(Map.of("message", "Unmatched successfully"));
    }
}