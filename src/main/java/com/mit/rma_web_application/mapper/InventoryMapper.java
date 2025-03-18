package com.mit.rma_web_application.mapper;

import com.mit.rma_web_application.dto.InventoryDto;
import com.mit.rma_web_application.model.Inventory;
import com.mit.rma_web_application.model.Vendor;

public class InventoryMapper {

    // Maps InventoryDto to Inventory entity, including Vendor association
    public static Inventory mapToInventory(InventoryDto inventoryDto, Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("Vendor cannot be null");
        }

        // Add null checks for optional fields in the DTO
        String inBoxPartNumber = (inventoryDto.getInBoxPartNumber() != null) ? inventoryDto.getInBoxPartNumber() : "";
        String boxPartNumber = (inventoryDto.getBoxPartNumber() != null) ? inventoryDto.getBoxPartNumber() : "";

        return new Inventory(
                inventoryDto.getId(),
                inventoryDto.getName(),
                inventoryDto.getMitNumber(),
                inventoryDto.getDescription(),
                inventoryDto.getQuantity(),
                inventoryDto.getInventoryLocation(),
                inventoryDto.getItemType(),
                inventoryDto.getPoNumber(),
                inventoryDto.getLotNumber(),
                vendor, // Vendor association
                inBoxPartNumber,
                boxPartNumber,
                inventoryDto.getInBoxSerialNumber(),
                inventoryDto.getBoxSerialNumber(),
                inventoryDto.getStatus(),
                inventoryDto.getCreatedAt(),
                inventoryDto.getUpdatedAt(),
                inventoryDto.getDeletedAt() // Soft delete field
        );
    }

    // Maps Inventory entity to InventoryDto, including Vendor ID (but not full Vendor object)
    public static InventoryDto mapToInventoryDto(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }

        return new InventoryDto(
                inventory.getId(),
                inventory.getName(),
                inventory.getMitNumber(),
                inventory.getDescription(),
                inventory.getQuantity(),
                inventory.getInventoryLocation(),
                inventory.getItemType(),
                inventory.getPoNumber(),
                inventory.getLotNumber(),
                inventory.getVendor() != null ? inventory.getVendor().getId() : null, // Only Vendor ID
                inventory.getInBoxPartNumber(),
                inventory.getBoxPartNumber(),
                inventory.getInBoxSerialNumber(),
                inventory.getBoxSerialNumber(),
                inventory.getStatus(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt(),
                inventory.getDeletedAt() // Soft delete field
        );
    }
}
