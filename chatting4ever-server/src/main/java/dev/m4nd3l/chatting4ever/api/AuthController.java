package dev.m4nd3l.chatting4ever.api;

import dev.m4nd3l.chatting4ever.database.model.PendingEmailVerificationCode;
import dev.m4nd3l.chatting4ever.database.model.PendingForgotPasswordCode;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.PendingEmailVerificationCodeService;
import dev.m4nd3l.chatting4ever.database.service.PendingForgotPasswordCodeService;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import dev.m4nd3l.chatting4ever.email.EmailSenderService;
import dev.m4nd3l.chatting4ever.utils.LongMapBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Set<Character> bannedUsernameChars = Set.of('@', ' ', '!', '#', '$', '%', '^', '&', '*', '(', ')', '+', '=', '{', '}', '[', ']', '|',
            '\\', ':', ';', '"', '\'', '<', '>', ',', '?', '/', '~', '`', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï', 'ñ',
            'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'ÿ', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ñ', 'Ò',
            'Ó', 'Ô', 'Õ', 'Ö', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', '\n');
    private static final Set<Character> bannedDisplayedNameChars = Set.of('!', '#', '$', '%', '^', '&', '*', '(', ')', '+', '=', '{', '}', '[', ']', '|', '\\',
            ':', ';', '"', '\'', '<', '>', '?', '/', '`', '~', '\n', '\r', '\t');
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern descriptionPattern = Pattern.compile("^[^\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]*$");
    private final UserService userService;
    private final PendingEmailVerificationCodeService emailVerificationCodeService;
    private final PendingForgotPasswordCodeService forgotPasswordCodeService;
    private final EmailSenderService emailSenderService;

    public AuthController(UserService userService, PendingEmailVerificationCodeService emailVerificationCodeService, PendingForgotPasswordCodeService forgotPasswordCode, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.emailVerificationCodeService = emailVerificationCodeService;
        this.emailSenderService = emailSenderService;
        this.forgotPasswordCodeService = forgotPasswordCode;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String displayedName = request.get("displayed-name");
        String email = request.get("email");
        String password = request.get("password");

        if (isSomeNull(username, displayedName, email, password)) return ResponseEntity.status(401).body(Map.of("error", "Missing some parameters", "success", false));

        if (!isEmailValid(email)) return ResponseEntity.status(400).body(Map.of("error", "Invalid email", "success", false));
        if (!isUsernameValid(username)) return ResponseEntity.status(400).body(Map.of("error", "Invalid username", "success", false));
        if (!isDisplayedNameValid(displayedName)) return ResponseEntity.status(400).body(Map.of("error", "Invalid displayed name", "success", false));

        if (userService.containsUsername(username)) return ResponseEntity.ok(Map.of("error", "Username taken", "success", false));
        if (userService.containsEmail(email)) return ResponseEntity.ok(Map.of("error", "Email already used", "success", false));

        if (!isPasswordValid(password)) return ResponseEntity.status(401).body(Map.of("error", "Password too weak", "success", false));

        User user = new User()
                .setUsername(username)
                .setProfileImageURL(ServletUriComponentsBuilder.fromCurrentContextPath().path("/profile-images/").path("default.png").toUriString())
                .setDisplayedName(displayedName)
                .setPublicEmail(false)
                .setPassword(password)
                .setEmail(email)
                .setVerifiedEmail(false)
                .setProfileDescription("")
                .setProfileNote("");
        userService.save(user);

        sendVerificationEmail(user);

        String token = JWTTokenProvider.generateToken(username);
        return ResponseEntity.ok(LongMapBuilder.of(
                "success", true,
                "token", token,
                "username", user.getUsername(),
                "profile-image-url", user.getProfileImageURL(),
                "displayed-name", user.getDisplayedName(),
                "online", user.isOnline(),
                "profile-description", user.getProfileDescription(),
                "profile-note", user.getProfileNote(),
                "created-at", user.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "public-email", user.isPublicEmail(),
                "email", user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        if (isSomeNull(password) || (username == null && email == null)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        User user = username == null ? userService.getUserByEmail(email) : userService.getUserByUsername(username);
        if (user == null || !user.checkPassword(password)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        String token = JWTTokenProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(LongMapBuilder.of(
                "success", true,
                "token", token,
                "username", user.getUsername(),
                "profile-image-url", user.getProfileImageURL(),
                "displayed-name", user.getDisplayedName(),
                "online", String.valueOf(user.isOnline()),
                "profile-description", user.getProfileDescription(),
                "profile-note", user.getProfileNote(),
                "created-at", user.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "public-email", String.valueOf(user.isPublicEmail()),
                "email", user.getEmail()));
    }

    @PostMapping("/change-profile-image")
    public ResponseEntity<Map<String, Object>> changeProfileImageURL(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String newProfileImageURL = request.get("new-profile-image-url");

        if (isSomeNull(token, newProfileImageURL)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        if (!isUsernameValid(newProfileImageURL)) return ResponseEntity.status(400).body(Map.of("error", "Invalid username", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map .of("error", "Invalid credentials", "success", false));

        user.setProfileImageURL(newProfileImageURL);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/change-username")
    public ResponseEntity<Map<String, Object>> changeUsername(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String newUsername = request.get("new-username");

        if (isSomeNull(token, newUsername)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        if (!isUsernameValid(newUsername)) return ResponseEntity.status(400).body(Map.of("error", "Invalid username", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map .of("error", "Invalid credentials", "success", false));
        if (userService.containsUsername(newUsername)) return ResponseEntity.status(409).body(Map.of("error", "Username already taken", "success", false));
        user.setUsername(newUsername);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true, "token", JWTTokenProvider.generateToken(newUsername)));
    }

    @PostMapping("/change-displayed-name")
    public ResponseEntity<Map<String, Object>> changeDisplayedName(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String newDisplayedName = request.get("new-displayed-name");

        if (isSomeNull(token, newDisplayedName)) return ResponseEntity.status(401).body(Map.of("error" , "Invalid credentials", "success", false));
        if (!isDisplayedNameValid(newDisplayedName)) return ResponseEntity.status(400).body(Map.of("error" , "Displayed name invalid", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error" , "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error" , "Invalid credentials", "success", false));

        user.setDisplayedName(newDisplayedName);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success" , true));
    }

    @PostMapping("/change-email")
    public ResponseEntity<Map<String, Object>> changeEmail(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String newEmail = request.get("new-email");

        if (isSomeNull(token, newEmail)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        if (!isEmailValid(newEmail)) return ResponseEntity.status(400).body(Map.of("error", "Invalid email", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        if (userService.containsEmail(newEmail)) return ResponseEntity.status(409).body(Map.of("error", "Email already used", "success", false));

        user.setEmail(newEmail);
        user.setVerifiedEmail(false);
        userService.save(user);

        sendVerificationEmail(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String oldPassword = request.get("old-password");
        String newPassword = request.get("new-password");

        if (isSomeNull(token, oldPassword, newPassword)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null || !user.checkPassword(oldPassword)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        if (!isPasswordValid(newPassword)) return ResponseEntity.status(400).body(Map.of("error", "Password too weak", "success", false));

        user.setPassword(newPassword);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/change-profile-description")
    public ResponseEntity<Map<String, Object>> changeProfileDescription(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String newProfileDescription = request.get("new-description");

        if (isSomeNull(token)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        if (!descriptionPattern.matcher(newProfileDescription).matches()) return ResponseEntity.status(401).body(Map.of("error", "Invalid description"));

        user.setProfileDescription(newProfileDescription == null ? "" : newProfileDescription);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/change-email-visibility")
    public ResponseEntity<Map<String, Object>> changeEmailVisibility(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        boolean newEmailVisibility = Boolean.parseBoolean(request.get("new-email-visibility"));

        if (isSomeNull(token)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        user.setPublicEmail(newEmailVisibility);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/change-profile-note")
    public ResponseEntity<Map<String, Object>> changeProfileNote(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String newProfileNote = request.get("new-note");

        if (isSomeNull(token)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        user.setProfileNote(newProfileNote == null ? "" : newProfileNote);
        userService.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String verificationCodeNumber = request.get("verification-code");

        if (isSomeNull(token, verificationCodeNumber)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        PendingEmailVerificationCode verificationCode;
        try { verificationCode = emailVerificationCodeService.getVerificationCodeByCode(Integer.parseInt(verificationCodeNumber)); }
        catch (Exception ignored) { return ResponseEntity.status(401).body(Map.of("error", "Invalid code", "success", false)); }
        if (verificationCode == null || verificationCode.hasExpired() || !verificationCode.verify(user))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid code", "success", false));

        user.setVerifiedEmail(true);

        userService.save(user);
        emailVerificationCodeService.delete(emailVerificationCodeService.getVerificationCodeByUserID(user.getID()));

        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/resend-verification-email")
    public ResponseEntity<Map<String, Object>> resendVerificationEmail(@RequestHeader("token") String token) {
        if (isSomeNull(token)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        if (user.isVerifiedEmail()) return ResponseEntity.status(400).body(Map.of("error", "Already verified", "success", false));

        sendVerificationEmail(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<Map<String, Object>> verifyForgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verificationCodeNumber = request.get("code");
        String newPassword = request.get("new-password");

        if (isSomeNull(email, verificationCodeNumber, newPassword) || !isEmailValid(email))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        User user = userService.getUserByEmail(email);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Email not found", "success", false));

        PendingForgotPasswordCode forgotPasswordCode;
        try { forgotPasswordCode = forgotPasswordCodeService.getForgotPasswordCodeByCode(Integer.parseInt(verificationCodeNumber)); }
        catch (Exception ignored) { return ResponseEntity.status(401).body(Map.of("error", "Invalid code", "success", false)); }
        if (forgotPasswordCode == null || forgotPasswordCode.hasExpired() || !forgotPasswordCode.verify(user))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid code", "success", false));
        if (!isPasswordValid(newPassword))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email", "success", false));

        user.setPassword(newPassword);

        userService.save(user);
        forgotPasswordCodeService.delete(forgotPasswordCodeService.getForgotPasswordCodeByUserID(user.getID()));

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> sendForgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (isSomeNull(email) || !isEmailValid(email))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email", "success", false));

        User user = userService.getUserByEmail(email);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Email not found", "success", false));

        sendForgotPasswordEmail(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Map<String, String> request, @RequestHeader("token") String token) {
        String password = request.get("password");

        if (isSomeNull(token, password)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));

        String username = JWTTokenProvider.validateTokenAndGetUsername(token);
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        User user = userService.getUserByUsername(username);
        if (user == null || !user.checkPassword(password)) return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials", "success", false));
        userService.delete(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    private void sendVerificationEmail(User user) {
        PendingEmailVerificationCode verificationCode = new PendingEmailVerificationCode()
                .generateID(emailVerificationCodeService)
                .setUserID(user.getID())
                .setEmail(user.getEmail())
                .setExpirationDate(LocalDateTime.now().plusMinutes(30));
        if (emailVerificationCodeService.containsEmail(user.getEmail()))
            emailVerificationCodeService.deleteAllByEmail(user.getEmail());

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

    private void sendForgotPasswordEmail(User user) {
        PendingForgotPasswordCode verificationCode = new PendingForgotPasswordCode()
                .generateID(forgotPasswordCodeService)
                .setUserID(user.getID())
                .setEmail(user.getEmail())
                .setExpirationDate(LocalDateTime.now().plusMinutes(30));
        if (forgotPasswordCodeService.containsEmail(user.getEmail()))
            forgotPasswordCodeService.deleteAllByEmail(user.getEmail());

        forgotPasswordCodeService.save(verificationCode);

        String subject = "Chatting4Ever - Reset your password";

        String htmlBody = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; padding: 20px; color: #333;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; background: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>" +
                "    <h2 style='color: #4A90E2; text-align: center; margin-bottom: 20px;'>Forgot your password?</h2>" +
                "    <p>Hi <strong>" + user.getDisplayedName() + "</strong>,</p>" +
                "    <p>Someone tried to login to your account but didn't remember the password, here's the code to reset it:</p>" +
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

    private boolean isDisplayedNameValid(String displayedName) {
        if (displayedName == null) return false;
        for (char character : displayedName.toCharArray()) if (bannedDisplayedNameChars.contains(character)) return false;
        return true;
    }

    private boolean isUsernameValid(String username) {
        if (username == null) return false;
        for (char character : username.toCharArray()) if (bannedUsernameChars.contains(character)) return false;
        return true;
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