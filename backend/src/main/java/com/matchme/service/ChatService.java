package com.matchme.service;

import com.matchme.config.ChatWebSocketHandler;
import com.matchme.dto.*;
import com.matchme.entity.*;  
import com.matchme.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConnectionRepository connectionRepository;
    
    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private ChatWebSocketHandler webSocketHandler;

    public boolean areUsersConnected(Long userId1, Long userId2) {
        System.out.println("Checking connection between users: " + userId1 + " and " + userId2);
        Optional<Connection> connection = connectionRepository.findConnectionBetweenUsers(userId1, userId2);
        System.out.println("Connection found: " + connection.isPresent());
        if (connection.isPresent()) {
            System.out.println("Connection status: " + connection.get().getStatus());
            System.out.println("Status is ACCEPTED: " + (connection.get().getStatus() == Connection.ConnectionStatus.ACCEPTED));
        }
        return connection.isPresent() && connection.get().getStatus() == Connection.ConnectionStatus.ACCEPTED;
    }

    public void validateUserInConversation(Long conversationId, Long userId) {
        Conversation conv = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        
        if (!conv.getUser1Id().equals(userId) && !conv.getUser2Id().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this conversation");
        }
    }
    
    @Transactional
    public Conversation getOrCreateConversation(Long user1Id, Long user2Id) {
        if (!areUsersConnected(user1Id, user2Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create conversation: users are not connected");
        }
        
        return conversationRepository.findByUsers(user1Id, user2Id)
            .orElseGet(() -> {
                Conversation conv = new Conversation(user1Id, user2Id);
                return conversationRepository.save(conv);
            });
    }
    
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        if (!areUsersConnected(senderId, receiverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot send message: users are not connected");
        }
        
        Conversation conversation = getOrCreateConversation(senderId, receiverId);
        
        Message message = new Message(conversation.getId(), senderId, receiverId, content);
        message = messageRepository.save(message);
        
        // Update conversation
        conversation.setLastMessageContent(content);
        conversation.setLastMessageAt(message.getTimestamp());
        conversation.incrementUnreadCount(receiverId);
        conversationRepository.save(conversation);
        
        return message;
    }
    
    public List<ConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository
            .findAllByUserIdOrderByLastMessageAtDesc(userId);
        
        List<ConversationDTO> dtos = new ArrayList<>();
        for (Conversation conv : conversations) {
            Long otherUserId = conv.getOtherUserId(userId);
            
            // FILTER: Only show conversations with connected users
            if (!areUsersConnected(userId, otherUserId)) {
                continue; // Skip this conversation
            }
            
            ConversationDTO dto = new ConversationDTO();
            dto.setId(conv.getId());
            dto.setOtherUserId(otherUserId);
            dto.setLastMessage(conv.getLastMessageContent());
            dto.setLastMessageAt(conv.getLastMessageAt());
            dto.setUnreadCount(conv.getUnreadCount(userId));
            
            // Get real user display name
            Optional<User> otherUser = userService.findById(otherUserId);
            if (otherUser.isPresent() && otherUser.get().getProfile() != null) {
                dto.setOtherUserName(otherUser.get().getProfile().getDisplayName());
            } else {
                dto.setOtherUserName("Unknown User");
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    public List<MessageDTO> getConversationMessages(Long conversationId, int page, int size) {
        Page<Message> messagePage = messageRepository.findByConversationIdOrderByTimestampDesc(
            conversationId, 
            PageRequest.of(page, size)
        );
        
        List<MessageDTO> dtos = new ArrayList<>();
        for (Message msg : messagePage.getContent()) {
            MessageDTO dto = new MessageDTO();
            dto.setId(msg.getId());
            dto.setConversationId(msg.getConversationId());
            dto.setSenderId(msg.getSenderId());
            dto.setReceiverId(msg.getReceiverId());
            dto.setContent(msg.getContent());
            dto.setTimestamp(msg.getTimestamp());
            dto.setRead(msg.isRead());
            dtos.add(dto);
        }
        
        Collections.reverse(dtos);
        return dtos;
    }
    
    @Transactional
    public void markMessagesAsRead(Long conversationId, Long readerId) {
        List<Message> unreadMessages = messageRepository.findByConversationIdAndReceiverIdAndIsRead(
            conversationId, 
            readerId, 
            false
        );
    
        System.out.println("🔍 Found " + unreadMessages.size() + " unread messages for user " + readerId + " in conversation " + conversationId);
    
    if (!unreadMessages.isEmpty()) {
        for (Message msg : unreadMessages) {
            System.out.println("  📧 Marking message " + msg.getId() + " as read (from user " + msg.getSenderId() + ")");
            msg.setRead(true);
        }
        messageRepository.saveAll(unreadMessages);
        System.out.println("✅ Successfully saved " + unreadMessages.size() + " messages as read to database");
        } else {
        System.out.println("⚠️ No unread messages found to mark as read");
     }
    }
    
    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        
        // Mark messages as read
        long unreadCount = conversation.getUnreadCount(userId);
            if (unreadCount == 0) {
                System.out.println("No unread messages for user " + userId + " in conversation " + conversationId + ", skipping");
                return; // Don't send notification if nothing to mark
            }

        System.out.println("Marking " + unreadCount + " messages as read for user " + userId + " in conversation " + conversationId);
    
        markMessagesAsRead(conversationId, userId);


        conversation.resetUnreadCount(userId);
        conversationRepository.save(conversation);

        // Send WebSocket notification to the other user
        Long otherUserId = conversation.getOtherUserId(userId);
        webSocketHandler.sendReadReceipt(conversationId, userId, otherUserId);
    }

    public Long getTotalUnreadCount(Long userId) {
        List<Conversation> conversations = conversationRepository
            .findAllByUserIdOrderByLastMessageAtDesc(userId);
    
        return conversations.stream()
            .mapToLong(conv -> conv.getUnreadCount(userId))
            .sum();
        }

}