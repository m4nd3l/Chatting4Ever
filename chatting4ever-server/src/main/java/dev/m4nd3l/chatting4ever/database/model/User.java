package dev.m4nd3l.chatting4ever.database.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Entity
@Table(name = "users")
public class User {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Column(name = "id") @GeneratedValue(strategy = GenerationType.IDENTITY) @Id private long ID;
    @Column(name = "displayed_name") private String displayedName;
    @Column(unique = true) private String username;
    @Column(unique = true) private String email;
    @Column(name = "verified_email") private boolean verifiedEmail;
    @Column(name = "public_email") private boolean publicEmail;
    @Column(name = "password_hash") private String passwordHash;
    @Column(name = "is_online") private boolean online;
    @Column(name = "profile_description") private String profileDescription;
    @Column(name = "profile_note") private String profileNote;
    @Column(name = "blocked_user_id") @CollectionTable(name = "blocked_users", joinColumns = @JoinColumn(name = "user_id")) @ElementCollection(fetch = FetchType.EAGER) private List<Long> blockedUsers;
    @Column(name = "created_at", nullable = false, updatable = false) @CreationTimestamp private LocalDateTime creationDate;
    @Column(name = "updated_at") @UpdateTimestamp private LocalDateTime updatedAt;

    public long getID() { return ID; }
    public String getUsername() { return username; }
    public String getDisplayedName() { return displayedName; }
    public String getEmail() { return email; }
    public boolean isVerifiedEmail() { return verifiedEmail; }
    public String getPassword() { return passwordHash; }
    public boolean isOnline() { return online; }
    public boolean isPublicEmail() { return publicEmail; }
    public String getProfileDescription() { return profileDescription; }
    public String getProfileNote() { return profileNote; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public LocalDateTime getUpdatedAtDate() { return updatedAt; }
    public boolean isBlocked(User user) { return isBlocked(user.getID()); }
    public boolean isBlocked(long userID) {
        if (blockedUsers == null || blockedUsers.isEmpty()) return false;
        return blockedUsers.contains(userID);
    }

    public User setID(long ID) { this.ID = ID; return this; }
    public User setDisplayedName(String displayedName) { this.displayedName = displayedName; return this; }
    public User setUsername(String username) { this.username = username; return this; }
    public User setEmail(String email) { this.email = email; return this; }
    public User setVerifiedEmail(boolean verifiedEmail) { this.verifiedEmail = verifiedEmail; return this; }
    public User setPassword(String rawPassword) { this.passwordHash = encoder.encode(rawPassword); return this; }
    public User setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
    public User setOnline(boolean online) { this.online = online; return this; }
    public User setPublicEmail(boolean publicEmail) { this.publicEmail = publicEmail; return this; }
    public User setProfileDescription(String profileDescription) { this.profileDescription = profileDescription; return this; }
    public User setProfileNote(String profileNote) { this.profileNote = profileNote; return this; }
    public User setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; return this; }
    public User setUpdateAtDate(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
    public User setBlockedUsers(List<Long> blockedUsers) { this.blockedUsers = blockedUsers; return this; }
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