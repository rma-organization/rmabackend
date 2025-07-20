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

    Optional<User> findByResetToken(String resetToken); // for password reset

    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);

    // Fetch all users with roles eagerly loaded
    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllUsersWithRoles();

    // Count total users
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    // Count users grouped by roles - returns list of Object[] {roles, count}
    @Query("SELECT u.roles, COUNT(u) FROM User u GROUP BY u.roles")
    List<Object[]> countUsersByRole();

    // Count pending approvals
    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.PENDING")
    long countPendingApprovals();

    // Count approved users between given dates
    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.APPROVED AND u.approvedAt BETWEEN :start AND :end")
    long countApprovedBetween(LocalDateTime start, LocalDateTime end);

    // Find recent users created since a date
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findRecentUsers(LocalDateTime since);
}
