package com.mit.rma_web_application.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.mit.rma_web_application.model.Vendor;

@Getter
@Setter
public class VendorDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    // Default constructor
    public VendorDTO() {}

    // Constructor that takes all the fields (to directly initialize VendorDTO)
    public VendorDTO(Long id, String name, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    // Constructor that converts a Vendor to VendorDTO
    public VendorDTO(Vendor vendor) {
        this.id = vendor.getId();
        this.name = vendor.getName();
        this.createdAt = vendor.getCreatedAt();
        this.deletedAt = vendor.getDeletedAt();
    }
}

