package com.matchme.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineStatusService {
    
    private final Map<Long, LocalDateTime> onlineUsers = new ConcurrentHashMap<>();
    private static final int ONLINE_THRESHOLD_MINUTES = 2;

    public void updateUserActivity(Long userId) {
        onlineUsers.put(userId, LocalDateTime.now());
    }

    public boolean isUserOnline(Long userId) {
        LocalDateTime lastActivity = onlineUsers.get(userId);
        if (lastActivity == null) {
            return false;
        }
        return lastActivity.isAfter(LocalDateTime.now().minusMinutes(ONLINE_THRESHOLD_MINUTES));
    }

    public void removeUser(Long userId) {
        onlineUsers.remove(userId);
    }
}