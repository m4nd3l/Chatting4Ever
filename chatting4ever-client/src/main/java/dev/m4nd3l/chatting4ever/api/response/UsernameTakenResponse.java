package dev.m4nd3l.chatting4ever.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsernameTakenResponse extends Response {
    @JsonProperty("username-taken") private boolean usernameTaken = false;

    public boolean isUsernameTaken() { return usernameTaken; }

    public UsernameTakenResponse setUsernameTaken(boolean usernameTaken) { this.usernameTaken = usernameTaken; return this; }
}