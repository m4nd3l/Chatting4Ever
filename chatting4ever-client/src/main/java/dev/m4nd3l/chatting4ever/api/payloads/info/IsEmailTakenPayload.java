package dev.m4nd3l.chatting4ever.api.payloads.info;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class IsEmailTakenPayload extends Payload {
    private String email;

    public IsEmailTakenPayload(String email) { this.email = email; }

    public String getEmail() { return email; }
    public IsEmailTakenPayload setEmail(String email) { this.email = email; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("email", getEmail())); }
}