package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaManagerController {
    private final UserService userService;

    public MediaManagerController(UserService userService) { this.userService = userService; }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<Map<String, Object>> uploadProfileImage(@RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        if (isSomeNull(token)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'token' header", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error" , "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error" , "Invalid credentials", "success", false));

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("server-data/uploads/profile-images/" + filename);
        try { Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING); }
        catch (Exception _) { return ResponseEntity.ok(Map.of("error", "An error occurred while saving the file", "success", false)); }
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/profile-images/").path(filename).toUriString();
        return ResponseEntity.ok(Map.of("success", true, "url", fileUrl));
    }

    private boolean isSomeNull(Object... params) {
        if (params == null) return true;
        for (Object parameter : params) if (parameter == null) return true;
        return false;
    }
}
