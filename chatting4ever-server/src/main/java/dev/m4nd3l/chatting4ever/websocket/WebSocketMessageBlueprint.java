package dev.m4nd3l.chatting4ever.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.m4nd3l.chatting4ever.database.model.messages.MediaContent;
import dev.m4nd3l.chatting4ever.database.model.messages.MessageType;

import java.time.LocalDateTime;

public class WebSocketMessageBlueprint {
    @JsonProperty("sender_username") private String senderUsername;
    @JsonProperty("receiver_username") private String receiverUsername;
    @JsonProperty("encoded_message") private String encodedMessage;
    @JsonProperty("type") private MessageType type;
    @JsonProperty("content") private MediaContent content;
    @JsonProperty("timestamp") private LocalDateTime timestamp;

    public MediaContent getContent() { return content; }
    public String getEncodedMessage() { return encodedMessage; }
    public String getReceiverUsername() { return receiverUsername; }
    public String getSenderUsername() { return senderUsername; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public MessageType getType() { return type; }

    public WebSocketMessageBlueprint setContent(MediaContent content) { this.content = content; return this; }
    public WebSocketMessageBlueprint setEncodedMessage(String encodedMessage) { this.encodedMessage = encodedMessage; return this; }
    public WebSocketMessageBlueprint setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; return this; }
    public WebSocketMessageBlueprint setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; return this; }
    public WebSocketMessageBlueprint setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
    public WebSocketMessageBlueprint setType(MessageType type) { this.type = type; return this; }
}
