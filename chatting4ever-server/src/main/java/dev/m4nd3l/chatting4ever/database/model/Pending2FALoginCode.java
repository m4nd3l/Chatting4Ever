package dev.m4nd3l.chatting4ever.database.model;

import dev.m4nd3l.chatting4ever.database.service.Pending2FALoginCodeService;
import dev.m4nd3l.chatting4ever.database.service.PendingEmailVerificationCodeService;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name = "pending_2fa_login_codes")
public class Pending2FALoginCode {
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

    public Pending2FALoginCode setID(long ID) { this.ID = ID; return this; }
    public Pending2FALoginCode setCode(int code) { this.code = code; return this; }
    public Pending2FALoginCode setEmail(String email) { this.email = email; return this; }
    public Pending2FALoginCode setUserID(long userID) { this.userID = userID; return this; }
    public Pending2FALoginCode setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; return this; }
    public Pending2FALoginCode setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; return this; }

    public boolean hasExpired() { return expirationDate.isBefore(LocalDateTime.now()); }
    public boolean verify(User user) { return Objects.equals(user.getID(), getUserID()) && getEmail().equals(user.getEmail()); }

    public Pending2FALoginCode generateID(Pending2FALoginCodeService service) {
        Random random = new Random();
        int candidateId;
        do { candidateId = 100000 + random.nextInt(900000); }
        while (service.containsCode(candidateId));
        code = candidateId;
        return this;
    }
}