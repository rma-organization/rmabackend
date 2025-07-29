package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverRoleOrderByTimestampDesc(String receiverRole);

    List<Notification> findByReceiverRoleAndReadFalseOrderByTimestampDesc(String receiverRole);

    long countByReceiverRoleAndReadIsFalse(String receiverRole);

    // General method for roles + username, if needed
    List<Notification> findByReceiverRoleAndUserNameOrderByTimestampDesc(String receiverRole, String userName);

    // Custom query for engineers to join Requests and get notifications where request.requestedBy = :userName
    @Query("SELECT n FROM Notification n " +
            "JOIN Request r ON n.requestsId = r.id " +
            "WHERE n.receiverRole = 'engineer' AND r.requestedBy = :userName " +
            "ORDER BY n.timestamp DESC")
    List<Notification> findEngineerNotificationsForUser(@Param("userName") String userName);
}
