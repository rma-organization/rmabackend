package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.models.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);

    Optional<User> findById(Long id);

    Optional<User> findByResetToken(String resetToken); // ✅ Added for password reset

    // ✅ Custom query to fetch users with roles
    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllUsersWithRoles();

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    @Query("SELECT u.roles, COUNT(u) FROM User u GROUP BY u.roles")
    List<Object[]> countUsersByRole();

    @Query("SELECT COUNT(u) FROM User u WHERE u.approvalStatus = com.mit.rma_web_application.models.ApprovalStatus.PENDING")
    long countPendingApprovals();

    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findRecentUsers(java.time.LocalDateTime since);
}
