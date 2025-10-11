package com.matchme.controller;

import com.matchme.dto.*;
import com.matchme.entity.Conversation;
import com.matchme.entity.User;
import com.matchme.repository.ConversationRepository;
import com.matchme.service.ChatService;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    private Long getUserIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        Optional<User> user = userService.findByEmail(email);
        return user.map(User::getId).orElse(null);
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        List<ConversationDTO> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        chatService.validateUserInConversation(conversationId, userId);
        
        // Check if users are still connected
        Conversation conv = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        Long otherUserId = conv.getOtherUserId(userId);
        
        if (!chatService.areUsersConnected(userId, otherUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Cannot view messages: users are no longer connected");
        }
        
        List<MessageDTO> messages = chatService.getConversationMessages(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/conversations/with/{otherUserId}")
    public ResponseEntity<?> getOrCreateConversation(
            @PathVariable Long otherUserId,
            Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        var conversation = chatService.getOrCreateConversation(userId, otherUserId);
        
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setOtherUserId(otherUserId);
        dto.setLastMessage(conversation.getLastMessageContent());
        dto.setLastMessageAt(conversation.getLastMessageAt());
        dto.setUnreadCount(conversation.getUnreadCount(userId));
        
        Optional<User> otherUser = userService.findById(otherUserId);
        if (otherUser.isPresent() && otherUser.get().getProfile() != null) {
            dto.setOtherUserName(otherUser.get().getProfile().getDisplayName());
        } else {
            dto.setOtherUserName("Unknown User");
        }
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        Long count = chatService.getTotalUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
        @RequestBody SendMessageRequest request,
        Principal principal) {
    
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
    
        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    
        try {
            chatService.sendMessage(userId, request.getReceiverId(), request.getContent());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending message: " + e.getMessage());
        }
    }

    @PostMapping("/conversations/{conversationId}/markRead")
    public ResponseEntity<?> markAsRead(
        @PathVariable Long conversationId,
        Principal principal) {
    
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        try {
            chatService.markConversationAsRead(conversationId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error marking as read: " + e.getMessage());
        }
    }
}