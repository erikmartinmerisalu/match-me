package com.matchme.service;

import com.matchme.entity.User;
import com.matchme.repository.ConnectionRepository;
import com.matchme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Long> getConnectedUserIds(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }
        
        User user = userOpt.get();
        // TODO: Implement actual connection logic
        // For now return empty list
        return Collections.emptyList();
    }
}