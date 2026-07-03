package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final UserService userService;

    public SearchController(UserService userService) { this.userService = userService; }

    @PostMapping("/info")
    public ResponseEntity<Map<String, String>> info(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (isSomeNull(username)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'username' parameter"));

        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(400).body(Map.of("error", "Couldn't find a user called '" + username + "'"));

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "displayed-name", user.getDisplayedName(),
                "online", String.valueOf(user.isOnline()),
                "profile-description", user.getProfileDescription(),
                "profile-note", user.getProfileNote(),
                "created-at", user.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "public-email", String.valueOf(user.isPublicEmail()),
                "email", user.isPublicEmail() ? user.getEmail() : ""
        ));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String limitString = request.get("limit");
        String startString = request.get("start");
        String countString = request.get("count");

        if (isSomeNull(query)) return ResponseEntity.status(400).body(Map.of("error", "Missing 'query' parameter"));

        int startIndex = 0, finishIndex = 0;
        List<User> matchedUsers = null;

        if (isAllNull(limitString, startString, countString)) {
            matchedUsers = userService.searchUsers(query);
        }

        if (!isSomeNull(startString, countString)) {
            startIndex = Integer.parseInt(startString);
            finishIndex = startIndex + Integer.parseInt(countString);
        } else if (!isSomeNull(limitString)) finishIndex = Integer.parseInt(limitString);

        if (matchedUsers == null) {

        }


        List<Map<String, String>> response = matchedUsers.stream().map(user -> Map.of(
                "username", user.getUsername(),
                "displayed-name", user.getDisplayedName(),
                "online", String.valueOf(user.isOnline()),
                "profile-description", user.getProfileDescription(),
                "profile-note", user.getProfileNote(),
                "created-at", user.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "public-email", String.valueOf(user.isPublicEmail()),
                "email", user.isPublicEmail() ? user.getEmail() : ""
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
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
