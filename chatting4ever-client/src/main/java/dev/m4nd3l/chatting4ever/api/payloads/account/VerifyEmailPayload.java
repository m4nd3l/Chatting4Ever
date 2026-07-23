package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class VerifyEmailPayload extends Payload {
    private String verificationCode;

    public VerifyEmailPayload(String verificationCode) { this.verificationCode = verificationCode; }

    public String getVerificationCode() { return verificationCode; }

    public VerifyEmailPayload setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; return this; }

    @Override
    public String getString() { return toJsonFormat(Map.of("verification-code", getVerificationCode())); }
}