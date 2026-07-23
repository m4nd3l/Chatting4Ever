package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangePasswordPayload extends Payload {
    private String oldPassword;
    private String newPassword;

    public ChangePasswordPayload(String oldPassword, String newPassword) { this.oldPassword = oldPassword; this.newPassword = newPassword; }

    public String getOldPassword() { return oldPassword; }
    public String getNewPassword() { return newPassword; }

    public ChangePasswordPayload setNewPassword(String newPassword) { this.newPassword = newPassword; return this; }
    public ChangePasswordPayload setOldPassword(String oldPassword) { this.oldPassword = oldPassword; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                "old-password", getOldPassword(),
                "new-password", getNewPassword()));
    }
}