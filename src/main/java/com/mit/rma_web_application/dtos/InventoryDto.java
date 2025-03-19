package com.mit.rma_web_application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Long id;
    private String name;
    private String mitNumber;
    private String description;
    private Integer quantity;
    private String inventoryLocation;
    private String itemType;
    private String poNumber;
    private String lotNumber;
    private Long vendorId;
    private String inBoxPartNumber;
    private String boxPartNumber;
    private String inBoxSerialNumber;
    private String boxSerialNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
