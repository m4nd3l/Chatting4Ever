package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class VerifyForgotPassword extends Payload {
    private String email;
    private String code;
    private String newPassword;

    public VerifyForgotPassword(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
    public String getNewPassword() { return newPassword; }

    public VerifyForgotPassword setEmail(String email) { this.email = email; return this; }
    public VerifyForgotPassword setCode(String code) { this.code = code; return this; }
    public VerifyForgotPassword setNewPassword(String newPassword) { this.newPassword = newPassword; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
            "email", getEmail(),
            "code", getCode(),
            "new-password", getNewPassword()));
    }
}