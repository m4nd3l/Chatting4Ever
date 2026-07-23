package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class LoginPayload extends Payload {
    private String usernameOrEmail;
    private String password;

    public LoginPayload(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public String getPassword() { return password; }

    public LoginPayload setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; return this; }
    public LoginPayload setPassword(String password) { this.password = password; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                getUsernameOrEmail().contains("@") ? "email" : "username", getUsernameOrEmail(),
                "password", getPassword()));
    }
}