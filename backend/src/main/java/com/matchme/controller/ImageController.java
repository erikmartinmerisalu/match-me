package com.matchme.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.entity.Connection;
import com.matchme.entity.Recommendation;
import com.matchme.entity.User;
import com.matchme.repository.ConnectionRepository;
import com.matchme.repository.RecommendationRepository;
import com.matchme.service.UserService;

@RestController
@RequestMapping("/uploads")
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;


    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename,
                                             @AuthenticationPrincipal User currentUser) throws IOException {

        Optional<User> ownerOpt = userService.findByProfilePic("/uploads/" + filename);
        if (ownerOpt.isEmpty()) return ResponseEntity.notFound().build();

        User owner = ownerOpt.get();

        if (!canViewProfile(currentUser, owner)) return ResponseEntity.status(403).build();

        Path path = Paths.get("uploads").resolve(filename).normalize();
        File file = path.toFile();

        if (!file.exists() || !file.getCanonicalPath().startsWith(new File("uploads").getCanonicalPath())) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(resource);
    }


    private boolean canViewProfile(User currentUser, User targetUser) {
        if (targetUser.getId().equals(currentUser.getId())) return true;

        Optional<Connection> connectionOpt = connectionRepository
                .findConnectionBetweenUsers(currentUser.getId(), targetUser.getId());

        if (connectionOpt.isPresent()) {
            Connection connection = connectionOpt.get();
            Connection.ConnectionStatus status = connection.getStatus();

            return status == Connection.ConnectionStatus.ACCEPTED ||
                   status == Connection.ConnectionStatus.PENDING;
        }

        Optional<Recommendation> recommendationOpt = recommendationRepository.findByUserId(currentUser.getId());
        if (recommendationOpt.isPresent()) {
            Recommendation rec = recommendationOpt.get();
            if (rec.getRecommendedUserIds().contains(targetUser.getId())) return true;
        }

        return false;
    }
}
