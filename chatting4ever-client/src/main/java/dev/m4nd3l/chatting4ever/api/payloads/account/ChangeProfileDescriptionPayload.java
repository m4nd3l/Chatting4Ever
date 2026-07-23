package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangeProfileDescriptionPayload extends Payload {
    private String newProfileDescription;

    public ChangeProfileDescriptionPayload(String newProfileDescription) { this.newProfileDescription = newProfileDescription; }

    public String getNewProfileDescription() { return newProfileDescription; }

    public ChangeProfileDescriptionPayload setNewProfileDescription(String newProfileDescription) { this.newProfileDescription = newProfileDescription; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-description", getNewProfileDescription())); }
}