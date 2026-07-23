package dev.m4nd3l.chatting4ever.api.payloads.search;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class InfoPayload extends Payload {
    private String username;

    public InfoPayload(String username) { this.username = username; }

    public String getUsername() { return username; }

    public InfoPayload setUsername(String username) { this.username = username; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("username", getUsername())); }
}
