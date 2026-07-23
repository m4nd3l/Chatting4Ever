package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ForgotPasswordPayload extends Payload {
    private String email;

    public ForgotPasswordPayload(String email) { this.email = email; }

    public String getEmail() { return email; }

    public ForgotPasswordPayload setEmail(String email) { this.email = email; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("email", getEmail())); }
}