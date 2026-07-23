package dev.m4nd3l.chatting4ever.api.payloads.search;

import dev.m4nd3l.chatting4ever.api.payloads.Payload;

import java.util.Map;

public class RangeSearchPayload extends Payload {
    private String username;
    private int start;
    private int count;

    public RangeSearchPayload(String username, int start, int count) { this.username = username; this.start = start; this.count = count; }

    public String getUsername() { return username; }
    public int getStart() { return start; }
    public int getCount() { return count; }

    public RangeSearchPayload setUsername(String username) { this.username = username; return this; }
    public RangeSearchPayload setStart(int start) { this.start = start; return this; }
    public RangeSearchPayload setCount(int count) { this.count = count; return this; }

    @Override
    public String getString() {
        return toJsonFormat(Map.of(
                "username", getUsername(),
                "start", String.valueOf(getStart()),
                "count", String.valueOf(getCount())));
    }
}
