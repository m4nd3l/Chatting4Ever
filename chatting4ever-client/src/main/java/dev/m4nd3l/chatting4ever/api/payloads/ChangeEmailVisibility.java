package dev.m4nd3l.chatting4ever.api.payloads;

import java.util.Map;

public class ChangeEmailVisibility extends Payload {
    private boolean newVisibility;

    public ChangeEmailVisibility(boolean newVisibility) { this.newVisibility = newVisibility; }

    public boolean getNewVisibility() { return newVisibility; }

    public ChangeEmailVisibility setNewVisibility(boolean newVisibility) { this.newVisibility = newVisibility; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-email-visibility", String.valueOf(getNewVisibility()))); }
}
