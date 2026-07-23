package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final UserService userService;

    public SearchController(UserService userService) { this.userService = userService; }

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> info(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String toSearch = request.get("username");

        if (isSomeNull(token)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'token' header", "success", false));
        if (isSomeNull(toSearch)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'username' parameter", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        User toSearchUser = userService.getUserByUsername(toSearch);
        if (toSearchUser == null) return ResponseEntity.status(400).body(Map.of("error", "Couldn't find a user called '" + toSearch + "'", "success", false));

        boolean isBlocked = toSearchUser.isBlocked(user.getID());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", isBlocked ? user.getUsername() : "blocked",
                "profile-image-url", user.getProfileImageURL(),
                "displayed-name", isBlocked ? user.getDisplayedName() : "blocked",
                "online", isBlocked ? String.valueOf(user.isOnline()) : "blocked",
                "profile-description", isBlocked ? user.getProfileDescription() : "blocked",
                "profile-note", isBlocked ? user.getProfileNote() : "blocked",
                "created-at", isBlocked ? user.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "blocked",
                "public-email", isBlocked ? String.valueOf(user.isPublicEmail()) : "blocked",
                "email", isBlocked ? user.isPublicEmail() ? user.getEmail() : "" : "blocked"
        ));
    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String query = request.get("query");
        String limitString = request.get("limit");
        String startString = request.get("start");
        String countString = request.get("count");

        if (isSomeNull(token)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'token' header", "success", false));
        if (isSomeNull(query)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'query' parameter", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        int startIndex;
        List<User> matchedUsers;

        if (!isSomeNull(startString, countString)) matchedUsers = userService.searchUsers(query, startIndex = Integer.parseInt(startString), startIndex + Integer.parseInt(countString));
        else if (!isSomeNull(limitString)) matchedUsers = userService.searchUsers(query, Integer.parseInt(limitString));
        else matchedUsers = userService.searchUsers(query);

        matchedUsers.removeIf(currentUser -> currentUser.isBlocked(user.getID()));

        List<Map<String, String>> response = matchedUsers.stream().map(currentUser -> Map.of(
                "username", currentUser.getUsername(),
                "profile-image-url", currentUser.getProfileImageURL(),
                "displayed-name", currentUser.getDisplayedName(),
                "online", String.valueOf(currentUser.isOnline()),
                "profile-description", currentUser.getProfileDescription(),
                "profile-note", currentUser.getProfileNote(),
                "created-at", currentUser.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "public-email", String.valueOf(currentUser.isPublicEmail()),
                "email", currentUser.isPublicEmail() ? currentUser.getEmail() : ""
        )).toList();

        return ResponseEntity.ok(Map.of("success", true, "users", response));
    }

    private boolean isSomeNull(Object... params) {
        if (params == null) return true;
        for (Object parameter : params) if (parameter == null) return true;
        return false;
    }

    private boolean isAllNull(Object... params) {
        if (params == null) return true;
        boolean result = true;
        for (Object parameter : params) result = result && parameter == null;
        return result;
    }
}