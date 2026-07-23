package dev.m4nd3l.chatting4ever.api.payloads.search;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class LimitSearchPayload extends Payload {
    private String username;
    private int limit;

    public LimitSearchPayload(String username, int limit) { this.username = username; this.limit = limit; }

    public String getUsername() { return username; }
    public int getLimit() { return limit; }

    public LimitSearchPayload setUsername(String username) { this.username = username; return this; }
    public LimitSearchPayload setLimit(int limit) { this.limit = limit; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                "username", getUsername(),
                "limit", String.valueOf(getLimit())));
    }
}
