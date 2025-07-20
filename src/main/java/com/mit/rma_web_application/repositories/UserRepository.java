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

    // Basic existence checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Find by identifiers
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByResetToken(String resetToken);

    // Filter by approval status
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);
    long countByApprovalStatus(ApprovalStatus status);

    // Spring Data JPA derived query for counting approved users between dates
    long countByApprovalStatusAndApprovedAtBetween(ApprovalStatus approvalStatus, LocalDateTime start, LocalDateTime end);

    // Fetch all active users (with soft delete support)
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActiveUsers();

    // Fetch users with roles eagerly fetched
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.deletedAt IS NULL")
    List<User> findAllUsersWithRoles();

    // Count total users
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    // Count users grouped by role
    @Query("SELECT u.roles, COUNT(u) FROM User u GROUP BY u.roles")
    List<Object[]> countUsersByRole();

    // Count users with pending approval
    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.PENDING")
    long countPendingApprovals();

    // Recently registered users since a date
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findRecentUsers(LocalDateTime since);
}
