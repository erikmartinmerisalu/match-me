package com.matchme.dto;

public class SendMessageRequest {
    private Long receiverId;
    private String content;
    
    public SendMessageRequest() {}
    
    // Getters and Setters
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}