package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUsername(String username);
}
