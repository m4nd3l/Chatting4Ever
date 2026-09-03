package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class RegisterPayload extends Payload {
    private String username;
    private String displayedName;
    private String email;
    private String password;
    boolean handOutToken;

    public RegisterPayload(String username, String displayedName, String email, String password, boolean handOutToken) {
        this.username = username;
        this.displayedName = displayedName;
        this.email = email;
        this.password = password;
        this.handOutToken = handOutToken;
    }

    public String getUsername() { return username; }
    public String getDisplayedName() { return displayedName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean handOutToken() { return handOutToken; }

    public RegisterPayload setUsername(String username) { this.username = username; return this; }
    public RegisterPayload setDisplayedName(String displayedName) { this.displayedName = displayedName; return this; }
    public RegisterPayload setEmail(String email) { this.email = email; return this; }
    public RegisterPayload setPassword(String password) { this.password = password; return this; }
    public RegisterPayload setHandOutToken(boolean handOutToken) { this.handOutToken = handOutToken; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                "username", getUsername(),
                "displayed-name", getDisplayedName(),
                "email", getEmail(),
                "password", getPassword(),
                "hand-out-token", String.valueOf(handOutToken())));
    }
}