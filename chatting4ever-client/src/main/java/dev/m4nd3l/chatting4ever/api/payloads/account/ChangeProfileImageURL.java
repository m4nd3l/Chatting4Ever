package dev.m4nd3l.chatting4ever.api.payloads.account;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class ChangeProfileImageURL extends Payload {
    private String url;

    public ChangeProfileImageURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public ChangeProfileImageURL setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String getString() { return toJsonFormat(Map.of("new-profile-image-url", getUrl())); }
}
