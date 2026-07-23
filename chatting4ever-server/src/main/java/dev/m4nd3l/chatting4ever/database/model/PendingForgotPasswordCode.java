package dev.m4nd3l.chatting4ever.database.model;

import dev.m4nd3l.chatting4ever.database.service.PendingEmailVerificationCodeService;
import dev.m4nd3l.chatting4ever.database.service.PendingForgotPasswordCodeService;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name = "pending_email_verification_codes")
public class PendingForgotPasswordCode {
    @Column(name = "id") @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long ID;
    @Column(unique = true) private int code;
    @Column(name = "user_id") private long userID;
    @Column(name = "email") private String email;
    @Column(name = "created_at", nullable = false, updatable = false) @CreationTimestamp private LocalDateTime creationDate;
    @Column(name = "expiration_date") private LocalDateTime expirationDate;

    public long getID() { return ID; }
    public int getCode() { return code; }
    public String getEmail() { return email; }
    public long getUserID() { return userID; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public LocalDateTime getExpirationDate() { return expirationDate; }

    public PendingForgotPasswordCode setID(long ID) { this.ID = ID; return this; }
    public PendingForgotPasswordCode setCode(int code) { this.code = code; return this; }
    public PendingForgotPasswordCode setEmail(String email) { this.email = email; return this; }
    public PendingForgotPasswordCode setUserID(long userID) { this.userID = userID; return this; }
    public PendingForgotPasswordCode setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; return this; }
    public PendingForgotPasswordCode setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; return this; }

    public boolean hasExpired() { return expirationDate.isAfter(LocalDateTime.now()); }
    public boolean verify(User user) { return Objects.equals(user.getID(), getUserID()) && getEmail().equals(user.getEmail()); }

    public PendingForgotPasswordCode generateID(PendingForgotPasswordCodeService service) {
        Random random = new Random();
        int candidateId;
        do { candidateId = 100000 + random.nextInt(900000); }
        while (service.containsCode(candidateId));
        code = candidateId;
        return this;
    }
}