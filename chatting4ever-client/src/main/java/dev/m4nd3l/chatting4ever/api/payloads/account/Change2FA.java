package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class Change2FA extends Payload {
    private boolean new2FA;

    public Change2FA(boolean newVisibility) { this.new2FA = newVisibility; }

    public boolean getNew2FA() { return new2FA; }

    public Change2FA setNew2FA(boolean new2FA) { this.new2FA = new2FA; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("2fa", String.valueOf(getNew2FA()))); }
}
