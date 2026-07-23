package dev.m4nd3l.chatting4ever.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadProfileImageResponse extends Response {
    @JsonProperty("url") private String url;

    public String getUrl() { return url; }

    public UploadProfileImageResponse setUrl(String url) { this.url = url; return this; }
}
