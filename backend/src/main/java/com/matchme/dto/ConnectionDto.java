package com.matchme.dto;

import com.matchme.entity.Connection;

public class ConnectionDto {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String status;

    public ConnectionDto(Connection connection) {
        this.id = connection.getId();
        this.fromUserId = connection.getFromUser().getId();
        this.toUserId = connection.getToUser().getId();
        this.status = connection.getStatus().toString();
    }

    // Getters
    public Long getId() { return id; }
    public Long getFromUserId() { return fromUserId; }
    public Long getToUserId() { return toUserId; }
    public String getStatus() { return status; }
}