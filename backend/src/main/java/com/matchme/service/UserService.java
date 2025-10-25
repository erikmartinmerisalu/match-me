package com.matchme.service;

import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.repository.UserRepository;
import com.matchme.repository.UserProfileRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    // ------------------------
    // Register with email & password only
    // ------------------------
    public User registerUser(String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(email, hashedPassword);


        // Save user first
        User savedUser = userRepository.save(user);

        // Create an empty profile for future population
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(profile);

        // Link profile to user
        savedUser.setProfile(profile);

        return savedUser;
    }


    // Authenticate

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }


    // Find by ID

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
    
    @Transactional
    public void deleteFakeUsers() {
        List<User> fakeUsers = userRepository.findAllByIsFake(true);
        
        System.out.println("Found " + fakeUsers.size() + " fake users to delete");
        
        for (User user : fakeUsers) {
            try {
                // Safely handle profile deletion
                if (user.getProfile() != null) {
                    userProfileRepository.delete(user.getProfile());
                    System.out.println("Deleted profile for user: " + user.getId());
                }
                userRepository.delete(user);
                System.out.println("Deleted user: " + user.getId());
            } catch (Exception e) {
                System.err.println("Error deleting user " + user.getId() + ": " + e.getMessage());
                // Continue with next user even if one fails
            }
        }
    }

    }

