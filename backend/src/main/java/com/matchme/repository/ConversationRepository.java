package com.matchme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.matchme.entity.Conversation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.user1Id = ?1 AND c.user2Id = ?2) OR " +
           "(c.user1Id = ?2 AND c.user2Id = ?1)")
    Optional<Conversation> findByUsers(Long userId1, Long userId2);
    
    @Query("SELECT c FROM Conversation c WHERE " +
           "c.user1Id = ?1 OR c.user2Id = ?1 " +
           "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findAllByUserIdOrderByLastMessageAtDesc(Long userId);
}