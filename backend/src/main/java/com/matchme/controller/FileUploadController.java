package com.matchme.controller;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/images")
public class FileUploadController {

    @Autowired
    private UserProfileService userProfileService;

    private final Path uploadDir = Paths.get("uploads");

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfilePicture(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file) {

        try {
            Long userId = currentUser.getId();

            String projectRoot = System.getProperty("user.dir");
            String uploadPath = projectRoot + "/uploads/";

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            UserProfile profile = currentUser.getProfile();
            if (profile.getProfilePic() != null) {
                String oldFilename = profile.getProfilePic().replace("/uploads/", "");
                File oldFile = new File(uploadPath + oldFilename);
                if (oldFile.exists()) oldFile.delete();
            }

            String original = file.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf("."));
            String filename = userId + extension;

            File destination = new File(uploadDir, filename);
            file.transferTo(destination);

            profile.setProfilePic("/uploads/" + filename);
            userProfileService.saveProfile(profile);

            return ResponseEntity.ok(Map.of(
                    "profilePic", "/uploads/" + filename
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeProfilePicture(@AuthenticationPrincipal User currentUser) {
    try {
        String projectRoot = System.getProperty("user.dir");
        String uploadPath = projectRoot + "/uploads/";

        UserProfile profile = currentUser.getProfile();

        if (profile.getProfilePic() == null) {
            return ResponseEntity.ok(Map.of("message", "No profile picture to remove"));
        }

        String filename = profile.getProfilePic().replace("/uploads/", "");
        File file = new File(uploadPath + filename);

        if (file.exists()) {
            file.delete();
        }

        profile.setProfilePic(null);
        userProfileService.saveProfile(profile);

        return ResponseEntity.ok(Map.of("message", "Profile picture removed"));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
}

}
