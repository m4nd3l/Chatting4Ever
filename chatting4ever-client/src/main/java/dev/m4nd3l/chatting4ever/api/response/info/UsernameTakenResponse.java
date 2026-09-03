package dev.m4nd3l.chatting4ever.api.response.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.m4nd3l.chatting4ever.api.response.auth.Response;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsernameTakenResponse extends Response {
    @JsonProperty("username-taken") private boolean usernameTaken = false;

    public boolean isUsernameTaken() { return usernameTaken; }

    public UsernameTakenResponse setUsernameTaken(boolean usernameTaken) { this.usernameTaken = usernameTaken; return this; }
}