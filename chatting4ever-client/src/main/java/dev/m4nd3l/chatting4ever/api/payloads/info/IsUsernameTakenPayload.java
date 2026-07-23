package dev.m4nd3l.chatting4ever.api.payloads.info;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class IsUsernameTakenPayload extends Payload {
    private String username;

    public IsUsernameTakenPayload(String username) { this.username = username; }

    public String getUsername() { return username; }
    public IsUsernameTakenPayload setUsername(String username) { this.username = username; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("username", getUsername())); }
}