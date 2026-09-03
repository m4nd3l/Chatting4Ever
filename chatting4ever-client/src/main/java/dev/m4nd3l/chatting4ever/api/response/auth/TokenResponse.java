package dev.m4nd3l.chatting4ever.api.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse extends Response {
    @JsonProperty("token") private String token;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
