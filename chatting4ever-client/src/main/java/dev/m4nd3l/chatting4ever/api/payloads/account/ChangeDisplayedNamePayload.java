package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangeDisplayedNamePayload extends Payload {
    private String newDisplayedName;

    public ChangeDisplayedNamePayload(String newDisplayedName) { this.newDisplayedName = newDisplayedName; }

    public String getNewDisplayedName() { return newDisplayedName; }

    public ChangeDisplayedNamePayload setNewDisplayedName(String newDisplayedName) { this.newDisplayedName = newDisplayedName; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-displayed-name", getNewDisplayedName())); }
}