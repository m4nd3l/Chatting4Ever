package dev.m4nd3l.chatting4ever.account;

import java.time.LocalDateTime;

public class AccountData {
    private static AccountData account;

    public static AccountData get() {
        return account == null ?
                new AccountData("unknown",
                        "unknown",
                        "unknown",
                        "unknown",
                        "unknown",
                        "unknown",
                        "unknown",
                        LocalDateTime.now(),
                        false) : account;
    }
    public static void setAccount(AccountData account) { AccountData.account = account; }

    private String token;
    private String username;
    private String displayedName;
    private String email;
    private String profileImageURL;
    private String profileDescription;
    private String profileNote;
    private LocalDateTime createdAt;
    private boolean publicEmail;

    public AccountData(String token, String username, String displayedName, String email, String profileImageURL, String profileDescription, String profileNote, LocalDateTime createdAt, boolean publicEmail) {
        this.token = token;
        this.username = username;
        this.displayedName = displayedName;
        this.email = email;
        this.profileImageURL = profileImageURL;
        this.profileDescription = profileDescription;
        this.profileNote = profileNote;
        this.createdAt = createdAt;
        this.publicEmail = publicEmail;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getDisplayedName() { return displayedName; }
    public String getEmail() { return email; }
    public String getProfileImageURL() { return profileImageURL; }
    public String getProfileDescription() { return profileDescription; }
    public String getProfileNote() { return profileNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPublicEmail() { return publicEmail; }

    public AccountData setToken(String token) { this.token = token; return this; }
    public AccountData setUsername(String username) { this.username = username; return this; }
    public AccountData setDisplayedName(String displayedName) { this.displayedName = displayedName; return this; }
    public AccountData setEmail(String email) { this.email = email; return this; }
    public AccountData setProfileImageURL(String profileImageURL) { this.profileImageURL = profileImageURL; return this; }
    public AccountData setProfileDescription(String profileDescription) { this.profileDescription = profileDescription; return this; }
    public AccountData setProfileNote(String profileNote) { this.profileNote = profileNote; return this; }
    public AccountData setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
    public AccountData setPublicEmail(boolean publicEmail) { this.publicEmail = publicEmail; return this; }
}
