package com.mit.rma_web_application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mit.rma_web_application.models.Customer;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByDeletedAtIsNull();
}
