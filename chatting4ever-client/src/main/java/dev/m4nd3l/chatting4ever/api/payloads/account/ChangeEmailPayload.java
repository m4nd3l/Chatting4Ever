package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangeEmailPayload extends Payload {
    private String newEmail;

    public ChangeEmailPayload(String newEmail) { this.newEmail = newEmail; }

    public String getNewEmail() { return newEmail; }

    public ChangeEmailPayload setNewEmail(String newEmail) { this.newEmail = newEmail; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-email", getNewEmail())); }
}