package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class GetDataLoginPayload extends Payload {
    private String usernameOrEmail;
    private String password;
    private String code;
    private boolean handOutToken;

    public GetDataLoginPayload(String usernameOrEmail, String password, String code, boolean handOutToken) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.code = code;
        this.handOutToken = handOutToken;
    }

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public String getPassword() { return password; }
    public String getCode() { return code; }
    public boolean handOutToken() { return handOutToken; }

    public GetDataLoginPayload setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; return this; }
    public GetDataLoginPayload setPassword(String password) { this.password = password; return this; }
    public GetDataLoginPayload setCode(String code) { this.code = code; return this; }
    public GetDataLoginPayload setHandOutToken(boolean handOutToken) { this.handOutToken = handOutToken; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                getUsernameOrEmail().contains("@") ? "email" : "username", getUsernameOrEmail(),
                "password", getPassword(), "code", getCode(),
                "hand-out-token", String.valueOf(handOutToken())));
    }
}