package dev.m4nd3l.chatting4ever.api.response.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.m4nd3l.chatting4ever.api.response.Response;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountAuthResponse extends Response {
    @JsonProperty("token") private String token = "2fa_check_first1&/";
    @JsonProperty("username") private String username;
    @JsonProperty("profile-image-url") private String profileImageURL;
    @JsonProperty("displayed-name") private String displayedName;
    @JsonProperty("online") private boolean online;
    @JsonProperty("profile-description") private String profileDescription;
    @JsonProperty("profile-note") private String profileNote;
    @JsonProperty("created-at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class) private LocalDateTime createdAt;
    @JsonProperty("public-email") private boolean publicEmail;
    @JsonProperty("verified-email") private boolean isEmailVerified;
    @JsonProperty("uses-2fa") private boolean uses2FA;
    @JsonProperty("email") private String email;
    @JsonProperty("uuid") private UUID uuid;

    public String getToken() { return token; }
    public String getProfileImageURL() { return profileImageURL; }
    public String getUsername() { return username; }
    public String getDisplayedName() { return displayedName; }
    public boolean isOnline() { return online; }
    public String getProfileDescription() { return profileDescription; }
    public String getProfileNote() { return profileNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPublicEmail() { return publicEmail; }
    public boolean isEmailVerified() { return isEmailVerified; }
    public String getEmail() { return email; }
    public boolean uses2FA() { return uses2FA; }
    public UUID getUUID() { return uuid; }

    public AccountAuthResponse setToken(String token) { this.token = token; return this; }
    public AccountAuthResponse setUsername(String username) { this.username = username; return this; }
    public AccountAuthResponse setProfileImageURL(String profileImageURL) { this.profileImageURL = profileImageURL; return this; }
    public AccountAuthResponse setDisplayedName(String displayedName) { this.displayedName = displayedName; return this; }
    public AccountAuthResponse setOnline(boolean online) { this.online = online; return this; }
    public AccountAuthResponse setProfileDescription(String profileDescription) { this.profileDescription = profileDescription; return this; }
    public AccountAuthResponse setProfileNote(String profileNote) { this.profileNote = profileNote; return this; }
    public AccountAuthResponse setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
    public AccountAuthResponse setPublicEmail(boolean publicEmail) { this.publicEmail = publicEmail; return this; }
    public AccountAuthResponse setEmailVerified(boolean emailVerified) { isEmailVerified = emailVerified; return this; }
    public AccountAuthResponse setEmail(String email) { this.email = email; return this; }
    public AccountAuthResponse setUses2FA(boolean uses2FA) { this.uses2FA = uses2FA; return this; }
    public AccountAuthResponse setUUID(UUID uuid) { this.uuid = uuid; return this; }

    public boolean has2FA() { return getToken().equals("2fa_check_first1&/"); }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountAuthResponse that = (AccountAuthResponse) o;
        return isOnline() == that.isOnline() && isPublicEmail() == that.isPublicEmail() && isEmailVerified() == that.isEmailVerified() && uses2FA == that.uses2FA && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getProfileImageURL(), that.getProfileImageURL()) && Objects.equals(getDisplayedName(), that.getDisplayedName()) && Objects.equals(getProfileDescription(), that.getProfileDescription()) && Objects.equals(getProfileNote(), that.getProfileNote()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getEmail(), that.getEmail());
    }
}