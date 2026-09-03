package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;
import java.util.UUID;

public class AutologinPayload extends Payload {
    private String usernameOrEmail;
    private UUID uuid;

    public AutologinPayload(String usernameOrEmail, UUID uuid) {
        this.usernameOrEmail = usernameOrEmail;
        this.uuid = uuid;
    }

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public UUID getUUID() { return uuid; }

    public AutologinPayload setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; return this; }
    public AutologinPayload setUUID(UUID uuid) { this.uuid = uuid; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                getUsernameOrEmail().contains("@") ? "email" : "username", getUsernameOrEmail(),
                "uuid", getUUID().toString()));
    }
}