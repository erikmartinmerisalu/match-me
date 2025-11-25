package com.matchme.config;

import com.matchme.service.UserService;
import com.matchme.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MatchWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.add(session);
            System.out.println("Match WebSocket connection established for user: " + userId);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Match WebSocket connection closed: " + status);
    }

    public void sendMatchUpdateToUser(Long userId) {
        String message = "{\"type\": \"MATCH_UPDATE\", \"timestamp\": " + System.currentTimeMillis() + "}";
        TextMessage textMessage = new TextMessage(message);
        
        for (WebSocketSession session : sessions) {
            Long sessionUserId = (Long) session.getAttributes().get("userId");
            if (userId.equals(sessionUserId)) {
                try {
                    session.sendMessage(textMessage);
                    System.out.println("Sent match update to user: " + userId);
                } catch (IOException e) {
                    System.err.println("Error sending match update to user " + userId + ": " + e.getMessage());
                }
            }
        }
    }
}