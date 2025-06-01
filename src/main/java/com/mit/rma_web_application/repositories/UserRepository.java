package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.models.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);

    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllUsersWithRoles();

    // Count by approval status
    long countByApprovalStatus(ApprovalStatus status);

    // Approved users between a date range
    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.APPROVED AND u.approvedAt BETWEEN :start AND :end")
    long countApprovedBetween(LocalDateTime start, LocalDateTime end);
}
