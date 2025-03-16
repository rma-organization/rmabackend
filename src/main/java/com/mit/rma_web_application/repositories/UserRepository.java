package com.mit.rma_web_application.repositories;
import com.mit.rma_web_application.model.User;
import com.mit.rma_web_application.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username); // Check if username exists
    boolean existsByEmail(String email);       // Check if email exists

    Optional<User> findByUsername(String username); // Find user by username
    Optional<User> findByEmail(String email);       // Find user by email
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus); // Find users by approval status

    Optional<User> findById(Long id); // Find user by ID (no need for @NonNull)
}
