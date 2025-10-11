package com.matchme.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long user1Id;
    
    @Column(nullable = false)
    private Long user2Id;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime lastMessageAt;
    
    @Column(length = 500)
    private String lastMessageContent;
    
    @Column(nullable = false)
    private Long unreadCountUser1 = 0L;
    
    @Column(nullable = false)
    private Long unreadCountUser2 = 0L;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
    }
    
    public Conversation() {}
    
    public Conversation(Long user1Id, Long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }
    
    public Long getOtherUserId(Long currentUserId) {
        return currentUserId.equals(user1Id) ? user2Id : user1Id;
    }
    
    public Long getUnreadCount(Long userId) {
        return userId.equals(user1Id) ? unreadCountUser1 : unreadCountUser2;
    }
    
    public void incrementUnreadCount(Long userId) {
        if (userId.equals(user1Id)) {
            unreadCountUser1++;
        } else {
            unreadCountUser2++;
        }
    }
    
    public void resetUnreadCount(Long userId) {
        if (userId.equals(user1Id)) {
            unreadCountUser1 = 0L;
        } else {
            unreadCountUser2 = 0L;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUser1Id() { return user1Id; }
    public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }
    
    public Long getUser2Id() { return user2Id; }
    public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public String getLastMessageContent() { return lastMessageContent; }
    public void setLastMessageContent(String lastMessageContent) { 
        this.lastMessageContent = lastMessageContent;
    }
    
    public Long getUnreadCountUser1() { return unreadCountUser1; }
    public void setUnreadCountUser1(Long unreadCountUser1) { 
        this.unreadCountUser1 = unreadCountUser1;
    }
    
    public Long getUnreadCountUser2() { return unreadCountUser2; }
    public void setUnreadCountUser2(Long unreadCountUser2) { 
        this.unreadCountUser2 = unreadCountUser2;
    }
}