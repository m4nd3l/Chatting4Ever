package dev.m4nd3l.chatting4ever.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailTakenResponse extends Response {
    @JsonProperty("email-taken") private boolean emailTaken = false;

    public boolean isEmailTaken() { return emailTaken; }

    public EmailTakenResponse setEmailTaken(boolean emailTaken) { this.emailTaken = emailTaken; return this; }
}