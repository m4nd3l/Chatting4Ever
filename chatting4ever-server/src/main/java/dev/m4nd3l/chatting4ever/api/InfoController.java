package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class InfoController {
    private final UserService userService;

    public InfoController(UserService userService) { this.userService = userService; }

    @PostMapping("/is-username-taken")
    public ResponseEntity<String> isUsernameTaken(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (isSomeNull(username)) return ResponseEntity.status(400).body("Missing 'username' parameter");

        return ResponseEntity.ok(String.valueOf(userService.containsUsername(username)));
    }

    @PostMapping("/is-email-taken")
    public ResponseEntity<String> isEmailTaken(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (isSomeNull(email)) return ResponseEntity.status(400).body("Missing 'email' parameter");

        return ResponseEntity.ok(String.valueOf(userService.containsEmail(email)));
    }

    private boolean isSomeNull(Object... params) {
        if (params == null) return true;
        for (Object parameter : params) if (parameter == null) return true;
        return false;
    }
}
