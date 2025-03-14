package com.mit.rma_web_application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "vendor")
    private List<Inventory> parts;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Optionally: Method for soft deletion of the vendor (if needed)
    public void softDelete() {
        this.deletedAt = LocalDateTime.now(); // Mark as soft deleted
    }
}
