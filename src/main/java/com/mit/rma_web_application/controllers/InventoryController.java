package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.InventoryDto;
import com.mit.rma_web_application.exceptions.ResourceNotFoundException;
import com.mit.rma_web_application.services.InventoryService;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@RestController
@RequestMapping("/api/inventory")
@Validated
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<?> createInventory(@Valid @RequestBody InventoryDto inventoryDto) {
        if (inventoryDto.getVendorId() == null || inventoryDto.getVendorId() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Vendor ID"));
        }

        try {
            InventoryDto savedInventory = inventoryService.createInventory(inventoryDto);
            return new ResponseEntity<>(savedInventory, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating inventory: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while creating the inventory item"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable("id") @Positive Long id) {
        try {
            InventoryDto inventoryDto = inventoryService.getInventoryById(id);
            return ResponseEntity.ok(inventoryDto);
        } catch (ResourceNotFoundException e) {
            logger.warn("Inventory not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Inventory not found"));
        }
    }

    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<InventoryDto> inventoryList = inventoryService.getAllInventory(page, size);
        return ResponseEntity.ok(inventoryList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(
            @PathVariable("id") @Positive Long id,
            @Valid @RequestBody InventoryDto updateInventory) {

        if (updateInventory.getVendorId() == null || updateInventory.getVendorId() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Vendor ID"));
        }

        try {
            InventoryDto updated = inventoryService.updateInventory(id, updateInventory);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            logger.warn("Inventory update failed for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/soft-delete/{id}")
    public ResponseEntity<?> softDeleteInventory(@PathVariable("id") @Positive Long id) {
        try {
            inventoryService.softDeleteInventory(id);
            return ResponseEntity.ok(Map.of("message", "Item soft deleted successfully"));
        } catch (ResourceNotFoundException e) {
            logger.warn("Soft delete failed for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found for deletion"));
        } catch (Exception e) {
            logger.error("Error during soft delete for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal error during soft deletion"));
        }
    }

    @PutMapping("/{inventoryId}/vendor/{vendorId}")
    public ResponseEntity<?> updateVendor(
            @PathVariable("inventoryId") @Positive Long inventoryId,
            @PathVariable("vendorId") @Positive Long vendorId
    ) {
        try {
            InventoryDto updatedInventory = inventoryService.updateVendor(inventoryId, vendorId);
            return ResponseEntity.ok(updatedInventory);
        } catch (ResourceNotFoundException e) {
            logger.warn("Update vendor failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
