
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

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.deletedAt IS NULL")
    List<User> findAllUsersWithRoles();
}
