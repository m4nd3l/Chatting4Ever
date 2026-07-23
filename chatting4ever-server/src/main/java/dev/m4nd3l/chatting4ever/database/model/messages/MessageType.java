package dev.m4nd3l.chatting4ever.database.model.messages;

public enum MessageType {
    TEXT("text"),
    AUDIO("audio"),
    IMAGE("image"),
    VIDEO("video"),
    STICKER("sticker"),
    GIF("gif");

    private String name;
    MessageType(String name) { this.name = name; }
    @Override public String toString() { return name; }
}