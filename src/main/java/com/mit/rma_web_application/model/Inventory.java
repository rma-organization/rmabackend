package com.mit.rma_web_application.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parts")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mit_number", nullable = false, unique = true)
    private String mitNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "inventory_location")
    private String inventoryLocation;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "lot_number")
    private String lotNumber;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "in_box_part_number")
    private String inBoxPartNumber;

    @Column(name = "box_part_number")
    private String boxPartNumber;

    @Column(name = "in_box_serial_number")
    private String inBoxSerialNumber;

    @Column(name = "box_serial_number")
    private String boxSerialNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Automatically set created and updated timestamps
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Soft delete: marks the record as deleted
    public void softDelete() {
        this.deletedAt = LocalDateTime.now(); // Mark as soft deleted
    }

    // Optionally: Method to check if the inventory item is soft deleted
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

