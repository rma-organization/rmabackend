// ChatMessageRepository.java
package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.models.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    @Query("SELECT m FROM ChatMessageEntity m WHERE " +
            "(m.sender = :sender AND m.receiver = :receiver) OR " +
            "(m.sender = :receiver AND m.receiver = :sender) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessageEntity> findMessagesBetweenUsers(@Param("sender") String sender,
                                                     @Param("receiver") String receiver);
    List<ChatMessageEntity> findByReceiverAndIsReadFalse(String receiver);

    @Query("SELECT COUNT(m) FROM ChatMessageEntity m WHERE m.receiver = :receiver AND m.sender = :sender AND m.isRead = false")
    long countUnreadMessages(@Param("sender") String sender, @Param("receiver") String receiver);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessageEntity m SET m.isRead = true WHERE m.sender = :sender AND m.receiver = :receiver AND m.isRead = false")
    void markMessagesAsRead(@Param("sender") String sender, @Param("receiver") String receiver);



}