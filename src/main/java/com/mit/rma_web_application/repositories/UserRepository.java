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
    Optional<User> findByResetToken(String resetToken);

    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);

    // ✅ Keep both active user queries and counting methods
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.deletedAt IS NULL")
    List<User> findAllUsersWithRoles();

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    @Query("SELECT u.roles, COUNT(u) FROM User u GROUP BY u.roles")
    List<Object[]> countUsersByRole();

    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.PENDING")
    long countPendingApprovals();

    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.APPROVED AND u.approvedAt BETWEEN :start AND :end")
    long countApprovedBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findRecentUsers(LocalDateTime since);

    long countByApprovalStatus(ApprovalStatus status); // Keep this from dev
}
