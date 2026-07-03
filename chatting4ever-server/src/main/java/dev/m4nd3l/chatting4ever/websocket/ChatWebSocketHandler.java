package dev.m4nd3l.chatting4ever.websocket;

import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private Map<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private UserService userService;

    public ChatWebSocketHandler(UserService userService) { this.userService = userService; }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userID = getUserIDFromSession(session);
        if (userID != null) {
            activeSessions.put(userID, session);
            userService.updateOnlineStatus(userID, true);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
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
    }

    private Long getUserIDFromSession(WebSocketSession session) { return (Long) session.getAttributes().get("userID"); }
}