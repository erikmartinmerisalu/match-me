// ConnectionRepository.java
package com.matchme.repository;

import com.matchme.entity.Connection;
import com.matchme.entity.Connection.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    
    @Query("SELECT c FROM Connection c WHERE (c.fromUser.id = :userId1 AND c.toUser.id = :userId2) OR (c.fromUser.id = :userId2 AND c.toUser.id = :userId1)")
    Optional<Connection> findConnectionBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    List<Connection> findByFromUserIdAndStatus(Long fromUserId, ConnectionStatus status);
    List<Connection> findByToUserIdAndStatus(Long toUserId, ConnectionStatus status);
    
    @Query("SELECT c FROM Connection c WHERE (c.fromUser.id = :userId OR c.toUser.id = :userId) AND c.status = 'ACCEPTED'")
    List<Connection> findAcceptedConnectionsForUser(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Connection c WHERE c.toUser.id = :userId AND c.status = 'PENDING'")
    List<Connection> findPendingConnectionsForUser(@Param("userId") Long userId);
}