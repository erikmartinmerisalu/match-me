package com.matchme.service;

import com.matchme.entity.Connection;
import com.matchme.entity.User;
import com.matchme.repository.ConnectionRepository;
import com.matchme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    // Add this inner class INSIDE ConnectionService class
    public static class ConnectionCreationResult {
        private Connection connection;
        private boolean created;
        
        public ConnectionCreationResult(Connection connection, boolean created) {
            this.connection = connection;
            this.created = created;
        }
        
        public Connection getConnection() { return connection; }
        public boolean isCreated() { return created; }
    }

    // Updated method that uses the inner class
    public ConnectionCreationResult findOrCreateConnection(Long fromUserId, Long toUserId, Connection.ConnectionStatus status) {
        // First, check if connection exists in either direction
        Optional<Connection> existingConnection = connectionRepository.findConnectionBetweenUsers(fromUserId, toUserId);
        
        if (existingConnection.isPresent()) {
            return new ConnectionCreationResult(existingConnection.get(), false); // was NOT created
        }
        
        // Create new connection
        User fromUser = userRepository.findById(fromUserId)
            .orElseThrow(() -> new RuntimeException("From user not found"));
        User toUser = userRepository.findById(toUserId)
            .orElseThrow(() -> new RuntimeException("To user not found"));
            
        Connection newConnection = new Connection(fromUser, toUser, status);
        Connection savedConnection = connectionRepository.save(newConnection);
        return new ConnectionCreationResult(savedConnection, true); // was created
    }
}