package com.matchme.dto;

import java.time.LocalDateTime;

public class ConversationDTO {
    private Long id;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
    
    public ConversationDTO() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOtherUserId() { return otherUserId; }
    public void setOtherUserId(Long otherUserId) { this.otherUserId = otherUserId; }
    
    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
    
    public String getOtherUserAvatar() { return otherUserAvatar; }
    public void setOtherUserAvatar(String otherUserAvatar) { this.otherUserAvatar = otherUserAvatar; }
    
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public Long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Long unreadCount) { this.unreadCount = unreadCount; }
}