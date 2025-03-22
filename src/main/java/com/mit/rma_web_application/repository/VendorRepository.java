package com.mit.rma_web_application.repository;

import com.mit.rma_web_application.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Fetch vendors that are not soft-deleted
    List<Vendor> findByDeletedAtIsNull();
}