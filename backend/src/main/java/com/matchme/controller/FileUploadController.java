package com.matchme.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.matchme.entity.User;
import com.matchme.entity.UserProfile;
import com.matchme.service.UserProfileService;
import com.matchme.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/images")
public class FileUploadController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    private final Path uploadDir = Paths.get("uploads");

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePic(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal(expression = "id") Long userId,
            HttpServletRequest request) {

        try {
            User currentUser = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = currentUser.getId() + extension;

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(filename);
            Files.write(filePath, file.getBytes());

            UserProfile profile = currentUser.getProfile();
            profile.setProfilePic("/uploads/" + filename);
            userProfileService.saveProfile(profile);

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String imageUrl = baseUrl + "/uploads/" + filename;

            return ResponseEntity.ok(Map.of(
                    "message", "Upload success",
                    "url", imageUrl
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeProfilePicture(@AuthenticationPrincipal(expression = "id") Long userId) {
        try {
            User currentUser = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserProfile profile = currentUser.getProfile();
            if (profile.getProfilePic() == null) {
                return ResponseEntity.ok(Map.of("message", "No profile picture to remove"));
            }

            String filename = profile.getProfilePic().replace("/uploads/", "");
            Path filePath = uploadDir.resolve(filename);
            File file = filePath.toFile();

            if (file.exists()) {
                file.delete();
            }

            profile.setProfilePic(null);
            userProfileService.saveProfile(profile);

            return ResponseEntity.ok(Map.of("message", "Profile picture removed"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
