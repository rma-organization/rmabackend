package com.mit.rma_web_application.repository;

import com.mit.rma_web_application.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Custom query method to find vendors that are not soft-deleted
    List<Vendor> findByDeletedAtIsNull();

    Optional<Vendor> findByIdAndDeletedAtIsNull(Long id); // To find by ID and ensure it's not soft deleted
}
