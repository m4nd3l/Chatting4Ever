package dev.m4nd3l.chatting4ever.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenAndInfoResponse extends Response {
    @JsonProperty("token") private String token;
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
    @JsonProperty("email") private String email;

    public String getToken() { return token; }
    public String getProfileImageURL() { return profileImageURL; }
    public String getUsername() { return username; }
    public String getDisplayedName() { return displayedName; }
    public TokenAndInfoResponse setToken(String token) { this.token = token; return this; }
    public boolean isOnline() { return online; }
    public String getProfileDescription() { return profileDescription; }
    public String getProfileNote() { return profileNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPublicEmail() { return publicEmail; }
    public String getEmail() { return email; }

    public TokenAndInfoResponse setUsername(String username) { this.username = username; return this; }
    public TokenAndInfoResponse setProfileImageURL(String profileImageURL) { this.profileImageURL = profileImageURL; return this; }
    public TokenAndInfoResponse setDisplayedName(String displayedName) { this.displayedName = displayedName; return this; }
    public TokenAndInfoResponse setOnline(boolean online) { this.online = online; return this; }
    public TokenAndInfoResponse setProfileDescription(String profileDescription) { this.profileDescription = profileDescription; return this; }
    public TokenAndInfoResponse setProfileNote(String profileNote) { this.profileNote = profileNote; return this; }
    public TokenAndInfoResponse setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
    public TokenAndInfoResponse setPublicEmail(boolean publicEmail) { this.publicEmail = publicEmail; return this; }
    public TokenAndInfoResponse setEmail(String email) { this.email = email; return this; }
}