package dev.m4nd3l.chatting4ever.database.model;

import dev.m4nd3l.chatting4ever.database.model.messages.MediaContent;
import dev.m4nd3l.chatting4ever.database.model.messages.MessageType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_messages")
public class PendingMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private Long senderID;
    private Long recipientID;
    private String encodedMessage;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    @Embedded
    private MediaContent content;
    private LocalDateTime timestamp;

    public Long getID() { return ID; }
    public Long getSenderID() { return senderID; }
    //public User getSender() { return senderID; }
    public Long getRecipientID() { return recipientID; }
    //public User getRecipient() { return recipientID; }
    public MediaContent getContent() { return content; }
    public MessageType getType() { return type; }
    public String getEncodedMessage() { return encodedMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
