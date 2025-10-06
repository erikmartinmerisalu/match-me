package com.matchme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matchme.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByConversationIdOrderByTimestampDesc(Long conversationId, Pageable pageable);
    
    Long countByConversationIdAndReceiverIdAndIsReadFalse(Long conversationId, Long receiverId);
}