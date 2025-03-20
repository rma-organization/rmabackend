package com.mit.rma_web_application.repositories;


import com.mit.rma_web_application.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Fetch all non-deleted inventory items
    List<Inventory> findByDeletedAtIsNull();

    // Fetch non-deleted inventory items with pagination
    Page<Inventory> findByDeletedAtIsNull(Pageable pageable);

    // Fetch a specific non-deleted inventory item by ID
    Optional<Inventory> findByIdAndDeletedAtIsNull(Long id);

    // Fetch inventory items by name (non-deleted)
    @Query("SELECT i FROM Inventory i WHERE i.deletedAt IS NULL AND i.name LIKE CONCAT('%', :name, '%')")
    Optional<List<Inventory>> findByNameContainingAndDeletedAtIsNull(@Param("name") String name);
}