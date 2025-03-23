package com.mit.rma_web_application.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;



    // Optionally: Method for soft deletion of the vendor (if needed)
    public void softDelete() {
        this.deletedAt = LocalDateTime.now(); // Mark as soft deleted
    }
}