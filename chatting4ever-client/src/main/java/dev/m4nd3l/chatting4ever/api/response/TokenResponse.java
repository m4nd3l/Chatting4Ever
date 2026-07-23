package dev.m4nd3l.chatting4ever.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse extends Response {
    @JsonProperty("displayed-name") private String token;

    public String getToken() { return token; }

    public TokenResponse setToken(String token) { this.token = token; return this; }
}
