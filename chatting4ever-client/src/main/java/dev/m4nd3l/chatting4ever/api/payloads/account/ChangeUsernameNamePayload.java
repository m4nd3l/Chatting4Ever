package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangeUsernameNamePayload extends Payload {
    private String newUsername;

    public ChangeUsernameNamePayload(String newUsername) { this.newUsername = newUsername; }

    public String getNewUsername() { return newUsername; }

    public ChangeUsernameNamePayload setNewUsername(String newUsername) { this.newUsername = newUsername; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-username", getNewUsername())); }
}