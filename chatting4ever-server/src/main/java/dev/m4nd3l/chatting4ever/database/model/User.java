package dev.m4nd3l.chatting4ever.database.model;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Entity
@Table(name = "users")
public class User {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String username;
    private String passwordHash;
    private boolean online;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_blocked", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "blocked_user_id")
    private List<Long> blockedUsers;

    public Long getID() { return ID; }
    public String getUsername() { return username; }
    public String getPassword() { return passwordHash; }
    public boolean isOnline() { return online; }
    public boolean isBlocked(User user) { return isBlocked(user.getID()); }
    public boolean isBlocked(long userID) {
        if (blockedUsers == null || blockedUsers.isEmpty()) return false;
        return blockedUsers.contains(userID);
    }

    public User setID(Long ID) { this.ID = ID; return this; }
    public User setUsername(String username) { this.username = username; return this; }
    public User setPassword(String rawPassword) { this.passwordHash = encoder.encode(rawPassword); return this; }
    public User setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
    public User setOnline(boolean online) { this.online = online; return this; }
    public User blockUser(User user) { blockUser(user.getID()); return this; }
    public User blockUser(Long userID) { actionOnBlockedList(list -> list.add(userID)); return this; }
    public User unblockUser(User user) { unblockUser(user.getID()); return this; }
    public User unblockUser(Long userID) { actionOnBlockedList(list -> list.remove(userID)); return this; }
    public User actionOnBlockedList(Consumer<List<Long>> action) {
        if (blockedUsers == null) blockedUsers = new ArrayList<>();
        action.accept(blockedUsers);
        return this;
    }

    public boolean checkPassword(String rawPassword) {
        if (passwordHash == null || rawPassword == null) return false;
        return encoder.matches(rawPassword, passwordHash);
    }
}