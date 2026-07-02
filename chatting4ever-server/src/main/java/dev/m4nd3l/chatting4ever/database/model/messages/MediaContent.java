package dev.m4nd3l.chatting4ever.database.model.messages;

import jakarta.persistence.Embeddable;

@Embeddable
public class MediaContent {
    private boolean isGIF;
    private String path;

    public boolean isGIF() { return isGIF; }
    public String getPath() { return path; }

    public MediaContent setGIF(boolean GIF) { isGIF = GIF; return this; }
    public MediaContent setPath(String path) { this.path = path; return this; }
}
