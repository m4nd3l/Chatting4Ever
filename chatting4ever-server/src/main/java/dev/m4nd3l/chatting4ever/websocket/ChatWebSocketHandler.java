package dev.m4nd3l.chatting4ever.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.m4nd3l.chatting4ever.database.model.PendingMessage;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.PendingMessageService;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private Map<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private UserService userService;
    private PendingMessageService pendingMessageService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ChatWebSocketHandler(UserService userService, PendingMessageService pendingMessageService) { this.userService = userService; this.pendingMessageService = pendingMessageService; }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        Long userID = getUserIDFromSession(session);
        if (userID != null) {
            activeSessions.put(userID, session);
            userService.updateOnlineStatus(userID, true);
            List<PendingMessage> messages = pendingMessageService.getMessagesByReceiverID(userID);
            if (messages == null || messages.isEmpty()) return;
            for (PendingMessage message : messages) sendPendingMessage(message, userID);
            pendingMessageService.deleteAllWithReceiverID(userID);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userID = getUserIDFromSession(session);
        if (userID != null) {
            activeSessions.remove(userID);
            userService.updateOnlineStatus(userID, false);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload == null || payload.isEmpty()) return;

        try {
            WebSocketMessageBlueprint incomingMessage = objectMapper.readValue(payload, WebSocketMessageBlueprint.class);

            User thisUser = userService.getUserByID(getUserIDFromSession(session));
            if (thisUser == null) return;
            if (!thisUser.getUsername().equals(incomingMessage.getSenderUsername())) return;

            String receiverUsername = incomingMessage.getReceiverUsername();
            if (receiverUsername == null || receiverUsername.isEmpty()) return;
            User receiver = userService.getUserByUsername(receiverUsername);
            if (receiver == null) return;
            Long receiverID = receiver.getID();
            if (receiverID == null) return;

            sendMessage(incomingMessage, payload, receiverID);
        } catch (Exception exception) { }
    }

    private void sendMessage(WebSocketMessageBlueprint messageBlueprint, String payload, long receiverID) throws IOException {
        WebSocketSession recipientSession = activeSessions.get(receiverID);
        if (recipientSession != null && recipientSession.isOpen()) recipientSession.sendMessage(new TextMessage(payload));
        else {
            User receiver = userService.getUserByUsername(messageBlueprint.getReceiverUsername());
            if (receiver == null) return;
            User sender = userService.getUserByUsername(messageBlueprint.getSenderUsername());
            if (sender == null) return;
            PendingMessage message = new PendingMessage()
                    .setReceiverID(receiver.getID())
                    .setSenderID(sender.getID())
                    .setEncodedMessage(messageBlueprint.getEncodedMessage())
                    .setContent(messageBlueprint.getContent())
                    .setType(messageBlueprint.getType())
                    .setTimestamp(messageBlueprint.getTimestamp());
            pendingMessageService.save(message);
        }
    }

    private void sendPendingMessage(PendingMessage message, long receiverID) throws IOException {
        WebSocketSession recipientSession = activeSessions.get(receiverID);

        User receiver = userService.getUserByID(message.getReceiverID());
        if (receiver == null) return;
        User sender = userService.getUserByID(message.getSenderID());
        if (sender == null) return;

        WebSocketMessageBlueprint blueprint = new WebSocketMessageBlueprint()
                .setContent(message.getContent())
                .setEncodedMessage(message.getEncodedMessage())
                .setType(message.getType())
                .setSenderUsername(sender.getUsername())
                .setReceiverUsername(receiver.getUsername())
                .setTimestamp(message.getTimestamp());
        if (recipientSession != null && recipientSession.isOpen())
            recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(blueprint)));
        else pendingMessageService.save(message);
    }

    private Long getUserIDFromSession(WebSocketSession session) { return (Long) session.getAttributes().get("userID"); }
}