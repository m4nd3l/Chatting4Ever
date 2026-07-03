package dev.m4nd3l.chatting4ever.websocket;

import dev.m4nd3l.chatting4ever.api.JWTTokenProvider;
import dev.m4nd3l.chatting4ever.database.model.User;
import dev.m4nd3l.chatting4ever.database.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

public class UniqueHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private final UserService userService;

    public UniqueHandshakeInterceptor(UserService userService) { this.userService = userService; }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            String token = servletRequest.getParameter("token");
            if (token == null || token.isEmpty()) return false;

            String username = JWTTokenProvider.validateTokenAndGetUsername(token);
            if (username == null || username.isEmpty()) return false;

            User user = userService.getUserByUsername(username);
            if (user == null) return false;

            attributes.put("userID", user.getID());
        }
        return true;
    }
}