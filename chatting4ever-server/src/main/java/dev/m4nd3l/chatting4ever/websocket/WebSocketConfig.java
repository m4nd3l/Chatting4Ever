package dev.m4nd3l.chatting4ever.websocket;

import dev.m4nd3l.chatting4ever.database.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private UserService userService;

    public WebSocketConfig(UserService userService) { this.userService = userService; }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(userService), "/ws/chat")
                .addInterceptors(new UniqueHandshakeInterceptor(userService))
                .setAllowedOrigins("*");
    }
}