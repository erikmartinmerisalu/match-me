package com.matchme.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchme.entity.Conversation;
import com.matchme.entity.Message;
import com.matchme.repository.ConversationRepository;
import com.matchme.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("WebSocket connected: User " + userId);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = (Long) session.getAttributes().get("userId");
        
        if (senderId == null) {
            System.err.println("ERROR: No userId in session");
            return;
        }
        
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");
        
        if ("message".equals(type)) {
            Long receiverId = ((Number) payload.get("receiverId")).longValue();
            String content = (String) payload.get("content");
            
            System.out.println("Received message from " + senderId + " to " + receiverId + ": " + content);
            
            // Save message to database
            Message savedMessage = chatService.sendMessage(senderId, receiverId, content);
            
            // Send confirmation back to sender
            Map<String, Object> senderConfirmation = Map.of(
                "type", "messageConfirmed",
                "messageId", savedMessage.getId(),
                "timestamp", savedMessage.getTimestamp().toString()
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(senderConfirmation)));
            
            // Send notification to receiver if online
            WebSocketSession receiverSession = userSessions.get(receiverId);
            if (receiverSession != null && receiverSession.isOpen()) {
                Map<String, Object> notification = Map.of(
                    "type", "newMessage",
                    "senderId", senderId,
                    "receiverId", receiverId,
                    "content", content,
                    "messageId", savedMessage.getId(),
                    "timestamp", savedMessage.getTimestamp().toString()
                );
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(notification)));
                System.out.println("Sent notification to receiver " + receiverId);
            } else {
                System.out.println("Receiver " + receiverId + " is offline");
            }
        } 
        else if ("typing".equals(type)) {
            Long recipientId = ((Number) payload.get("recipientId")).longValue();
            Boolean isTyping = (Boolean) payload.get("isTyping");
            
            System.out.println("User " + senderId + " typing status: " + isTyping + " to user " + recipientId);
            
            WebSocketSession recipientSession = userSessions.get(recipientId);
            if (recipientSession != null && recipientSession.isOpen()) {
                Map<String, Object> typingNotification = Map.of(
                    "type", "userTyping",
                    "userId", senderId,
                    "isTyping", isTyping
                );
                recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(typingNotification)));
            }
        }
        else if ("markAsRead".equals(type)) {
            Long conversationId = ((Number) payload.get("conversationId")).longValue();
            
            System.out.println("User " + senderId + " marking conversation " + conversationId + " as read");
            
            // Mark as read in database
            chatService.markConversationAsRead(conversationId, senderId);
            
            // Notify the other person that their messages were read
            Conversation conv = conversationRepository.findById(conversationId).orElse(null);
            if (conv != null) {
                Long otherUserId = conv.getOtherUserId(senderId);
                WebSocketSession otherSession = userSessions.get(otherUserId);
                
                if (otherSession != null && otherSession.isOpen()) {
                    Map<String, Object> readReceipt = Map.of(
                        "type", "messageRead",
                        "conversationId", conversationId,
                        "readByUserId", senderId,
                        "timestamp", System.currentTimeMillis()
                    );
                    otherSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(readReceipt)));
                    System.out.println("Sent read receipt to user " + otherUserId);
                }
            }
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("WebSocket disconnected: User " + userId);
        }
    }
}