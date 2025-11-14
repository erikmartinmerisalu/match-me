package com.matchme.config;

import com.matchme.service.UserService;
import com.matchme.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;
    
    @Autowired
    private MatchWebSocketHandler matchWebSocketHandler; // ADDED
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Chat WebSocket
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*")
                .addInterceptors(createHandshakeInterceptor());

        // Match WebSocket - ADDED
        registry.addHandler(matchWebSocketHandler, "/ws/matches")
                .setAllowedOrigins("*")
                .addInterceptors(createHandshakeInterceptor());
    }

    private HandshakeInterceptor createHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                         WebSocketHandler wsHandler, Map<String, Object> attributes) {
                // Extract JWT from cookie
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    Cookie[] cookies = servletRequest.getServletRequest().getCookies();
                    
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("jwt".equals(cookie.getName())) {
                                String token = cookie.getValue();
                                if (jwtUtil.validateToken(token)) {
                                    String email = jwtUtil.extractUsername(token);
                                    userService.findByEmail(email).ifPresent(user -> {
                                        attributes.put("userId", user.getId());
                                    });
                                }
                            }
                        }
                    }
                }
                return true;
            }
            
            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                     WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }
}