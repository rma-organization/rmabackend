package com.mit.rma_web_application.repositories;

import com.mit.rma_web_application.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByDeletedAtIsNull();

    Page<Inventory> findByDeletedAtIsNull(Pageable pageable);

    Optional<Inventory> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT i FROM Inventory i WHERE i.deletedAt IS NULL AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Optional<List<Inventory>> findByNameContainingAndDeletedAtIsNull(@Param("name") String name);

    @Query("SELECT i FROM Inventory i WHERE i.deletedAt IS NULL AND LOWER(i.inBoxPartNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Inventory> searchByPartNumber(@Param("query") String query);

    // New method to search by inBoxSerialNumber, ignoring case and soft-deleted
    @Query("SELECT i FROM Inventory i WHERE i.deletedAt IS NULL AND LOWER(i.inBoxSerialNumber) LIKE LOWER(CONCAT('%', :serial, '%'))")
    List<Inventory> searchBySerial(@Param("serial") String serial);
}
