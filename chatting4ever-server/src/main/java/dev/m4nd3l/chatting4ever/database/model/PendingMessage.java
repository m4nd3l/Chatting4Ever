package dev.m4nd3l.chatting4ever.database.model;

import dev.m4nd3l.chatting4ever.database.model.messages.MediaContent;
import dev.m4nd3l.chatting4ever.database.model.messages.MessageType;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_messages")
public class PendingMessage {
    @Column(name = "id") @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long ID;
    @Column(name = "sender_id") private long senderID;
    @Column(name = "recipient_id") private long recipientID;
    @Column(name = "encoded_message") private String encodedMessage;
    @Column(name = "message_type") @Enumerated(EnumType.STRING) private MessageType type;
    @Column(name = "message_content") @Embedded private MediaContent content;
    @Column(name = "timestamp") private LocalDateTime timestamp;

    public long getID() { return ID; }
    public long getSenderID() { return senderID; }
    public long getRecipientID() { return recipientID; }
    public User getRecipient(UserService service) { return service.getUserByID(getRecipientID()); }
    public User getSender(UserService service) { return service.getUserByID(getSenderID()); }
    public MediaContent getContent() { return content; }
    public MessageType getType() { return type; }
    public String getEncodedMessage() { return encodedMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public PendingMessage setContent(MediaContent content) { this.content = content; return this; }
    public PendingMessage setEncodedMessage(String encodedMessage) { this.encodedMessage = encodedMessage; return this; }
    public PendingMessage setID(long ID) { this.ID = ID; return this; }
    public PendingMessage setRecipientID(long recipientID) { this.recipientID = recipientID; return this; }
    public PendingMessage setSenderID(long senderID) { this.senderID = senderID; return this; }
    public PendingMessage setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
    public PendingMessage setType(MessageType type) { this.type = type; return this; }
}
