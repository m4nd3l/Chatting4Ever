package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class DeletePayload extends Payload {
    private String password;

    public DeletePayload(String password) { this.password = password; }

    public String getPassword() { return password; }

    public DeletePayload setPassword(String password) { this.password = password; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("password", getPassword())); }
}