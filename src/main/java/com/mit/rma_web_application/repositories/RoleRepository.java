package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.model.Role;
import com.mit.rma_web_application.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name); // Find role by Enum Name
}
