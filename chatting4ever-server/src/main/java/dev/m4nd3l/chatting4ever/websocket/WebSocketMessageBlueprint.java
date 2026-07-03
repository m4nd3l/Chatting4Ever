package dev.m4nd3l.chatting4ever.websocket;

import dev.m4nd3l.chatting4ever.database.model.messages.MediaContent;
import dev.m4nd3l.chatting4ever.database.model.messages.MessageType;

public class WebSocketMessageBlueprint {
    private Long recipientID;
    private String encodedMessage;
    private MessageType type;
    private MediaContent content;

    public Long getRecipientID() { return recipientID; }
    public String getEncodedMessage() { return encodedMessage; }
    public MessageType getType() { return type; }
    public MediaContent getContent() { return content; }
}
