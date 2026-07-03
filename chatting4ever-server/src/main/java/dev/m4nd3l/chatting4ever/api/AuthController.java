package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.model.PendingEmailVerificationCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.PendingEmailVerificationCodeService;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import dev.m4nd3l.chatting4ever.email.EmailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final UserService userService;
    private final PendingEmailVerificationCodeService emailVerificationCodeService;
    private final EmailSenderService emailSenderService;

    public AuthController(UserService userService, PendingEmailVerificationCodeService emailVerificationCodeService, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.emailVerificationCodeService = emailVerificationCodeService;
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String displayedName = request.get("displayed-name");
        String email = request.get("email");
        String password = request.get("password");

        if (isSomeNull(username, displayedName, email, password)) return ResponseEntity.status(401).body(Map.of("error", "Missing some parameters"));

        if (!isEmailValid(email)) return ResponseEntity.status(400).body(Map.of("error", "Invalid email"));

        if (userService.containsUsername(username)) return ResponseEntity.ok(Map.of("error", "Username taken"));
        if (userService.containsEmail(email)) return ResponseEntity.ok(Map.of("error", "Email already used"));

        if (!isPasswordValid(password)) return ResponseEntity.status(401).body(Map.of("error", "Password too weak"));

        User user = new User()
                .setUsername(username)
                .setDisplayedName(displayedName)
                .setPassword(password)
                .setEmail(email)
                .setVerifiedEmail(false)
                .setProfileDescription("")
                .setProfileNote("");
        userService.save(user);

        sendVerificationEmail(user);

        String token = JWTTokenProvider.generateToken(username);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        if (isSomeNull(password) || (username == null && email == null)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        User user = username == null ? userService.getUserByEmail(email) : userService.getUserByUsername(username);
        if (user == null || !user.checkPassword(password)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        String token = JWTTokenProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/change-username")
    public ResponseEntity<Map<String, String>> changeUsername(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newUsername = request.get("new-username");

        if (isSomeNull(token, newUsername)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        if (userService.containsUsername(newUsername)) return ResponseEntity.status(409).body(Map.of("error", "Username already taken"));
        user.setUsername(newUsername);
        userService.save(user);

        return ResponseEntity.ok(Map.of("token", JWTTokenProvider.generateToken(newUsername)));
    }

    @PostMapping("/change-displayed-name")
    public ResponseEntity<String> changeDisplayedName(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newDisplayedName = request.get("new-displayed-name");

        if (isSomeNull(token, newDisplayedName)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");

        user.setDisplayedName(newDisplayedName);
        userService.save(user);

        return ResponseEntity.ok("Displayed name changed successfully");
    }

    @PostMapping("/change-email")
    public ResponseEntity<String> changeEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newEmail = request.get("new-email");

        if (isSomeNull(token, newEmail)) return ResponseEntity.status(401).body("Invalid credentials");

        if (!isEmailValid(newEmail)) return ResponseEntity.status(400).body("Invalid email");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");
        if (userService.containsEmail(newEmail)) return ResponseEntity.status(409).body("Email already used");

        user.setEmail(newEmail);
        user.setVerifiedEmail(false);
        userService.save(user);

        sendVerificationEmail(user);

        return ResponseEntity.ok("Email changed successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String oldPassword = request.get("old-password");
        String newPassword = request.get("new-password");

        if (isSomeNull(token, oldPassword, newPassword)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null || !user.checkPassword(oldPassword)) return ResponseEntity.status(401).body("Invalid credentials");
        if (!isPasswordValid(newPassword)) return ResponseEntity.status(400).body("Password too weak");

        user.setPassword(newPassword);
        userService.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/change-profile-description")
    public ResponseEntity<String> chengeProfileDescription(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newProfileDescription = request.get("new-description");

        if (isSomeNull(token)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");

        user.setProfileDescription(newProfileDescription == null ? "" : newProfileDescription);
        userService.save(user);

        return ResponseEntity.ok("Profile description changed successfully");
    }

    @PostMapping("/change-profile-note")
    public ResponseEntity<String> chengeProfileNote(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newProfileNote = request.get("new-note");

        if (isSomeNull(token)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");

        user.setProfileNote(newProfileNote == null ? "" : newProfileNote);
        userService.save(user);

        return ResponseEntity.ok("Profile note changed successfully");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String verificationCodeNumber = request.get("verification-code");

        if (isSomeNull(token, verificationCodeNumber)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");

        PendingEmailVerificationCode verificationCode;
        try { verificationCode = emailVerificationCodeService.getVerificationCodeByCode(Integer.parseInt(verificationCodeNumber)); }
        catch (Exception ignored) { return ResponseEntity.status(401).body("Invalid code"); }
        if (verificationCode == null || !verificationCode.verify(user)) return ResponseEntity.status(401).body("Invalid code");

        user.setVerifiedEmail(true);

        userService.save(user);
        emailVerificationCodeService.delete(emailVerificationCodeService.getVerificationCodesByUserID(user.getID()));

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (isSomeNull(token)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");
        if (user.isVerifiedEmail()) return ResponseEntity.status(400).body("Already verified");

        sendVerificationEmail(user);

        return ResponseEntity.ok("Verification email resent successfully");
    }


    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");

        if (isSomeNull(token, password)) return ResponseEntity.status(401).body("Invalid credentials");

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User user = userService.getUserByUsername(username);
        if (user == null || !user.checkPassword(password)) return ResponseEntity.status(401).body("Invalid credentials");
        userService.delete(user);

        return ResponseEntity.ok("User deleted successfully");
    }

    private void sendVerificationEmail(User user) {
        PendingEmailVerificationCode verificationCode = new PendingEmailVerificationCode()
                .generateID(emailVerificationCodeService)
                .setUserID(user.getID())
                .setEmail(user.getEmail())
                .setExpirationDate(LocalDateTime.now().plusMinutes(30));
        if (emailVerificationCodeService.containsEmail(user.getEmail())) {
            emailVerificationCodeService.deleteAllByEmail(user.getEmail());
        }
        emailVerificationCodeService.save(verificationCode);

        String subject = "Chatting4Ever - Verify your account";

        String htmlBody = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; padding: 20px; color: #333;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; background: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>" +
                "    <h2 style='color: #4A90E2; text-align: center; margin-bottom: 20px;'>Welcome to Chatting4Ever!</h2>" +
                "    <p>Hi <strong>" + user.getDisplayedName() + "</strong>,</p>" +
                "    <p>To access all features you have to verify your email. Use the code displayed below to verify your email:</p>" +
                "    <div style='text-align: center; margin: 30px 0;'>" +
                "      <span style='background-color: #4A90E2; color: white; padding: 12px 35px; font-size: 24px; font-weight: bold; letter-spacing: 4px; border-radius: 5px; display: inline-block;'>" +
                verificationCode.getCode() +
                "      </span>" +
                "    </div>" +
                "    <p>Expiration date: " + verificationCode.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p>" +
                "    <p style='font-size: 12px; color: #777; text-align: center; margin-top: 30px;'>If you didn't request this email you can ignore it.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        try { emailSenderService.sendHtmlEmail(user.getEmail(), subject, htmlBody); }
        catch (Exception ignore) { }
    }

    private boolean isPasswordValid(String password) {
        if (password == null) return false;
        return passwordPattern.matcher(password).matches();
    }

    private boolean isEmailValid(String email) {
        if (email == null) return false;
        return emailPattern.matcher(email).matches();
    }

    private boolean isSomeNull(Object... params) {
        if (params == null) return true;
        for (Object parameter : params) if (parameter == null) return true;
        return false;
    }
}
