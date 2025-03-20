package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.InventoryDto;
import com.mit.rma_web_application.exception.ResourceNotFoundException;
import com.mit.rma_web_application.service.InventoryService;

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

    // Create a new inventory item
    @PostMapping
    public ResponseEntity<?> createInventory(@Valid @RequestBody InventoryDto inventoryDto) {
        if (inventoryDto.getVendorId() == null || inventoryDto.getVendorId() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Vendor ID"));
        }

        try {
            InventoryDto savedInventory = inventoryService.createInventory(inventoryDto);
            return new ResponseEntity<>(savedInventory, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while creating the inventory item"));
        }
    }

    // Get a specific inventory item by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable("id") @Positive Long inventoryId) {
        try {
            InventoryDto inventoryDto = inventoryService.getInventoryById(inventoryId);
            return ResponseEntity.ok(inventoryDto);
        } catch (ResourceNotFoundException e) {
            logger.error("Inventory not found with id: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Inventory not found"));
        }
    }

    // Get a paginated list of all inventory items
    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<InventoryDto> inventoryList = inventoryService.getAllInventory(page, size);
        return ResponseEntity.ok(inventoryList);
    }

    // Update an inventory item
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(
            @PathVariable("id") @Positive Long inventoryId,
            @Valid @RequestBody InventoryDto updateInventory) {
        if (updateInventory.getVendorId() == null || updateInventory.getVendorId() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Vendor ID"));
        }

        try {
            InventoryDto updatedInventory = inventoryService.updateInventory(inventoryId, updateInventory);
            return ResponseEntity.ok(updatedInventory);
        } catch (ResourceNotFoundException e) {
            logger.error("Error updating inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Inventory not found"));
        }
    }

    // Soft delete an inventory item
    @PutMapping("/soft-delete/{id}")
    public ResponseEntity<?> softDeleteInventory(@PathVariable Long id) {
        try {
            inventoryService.softDeleteInventory(id);
            return ResponseEntity.ok("Item soft deleted successfully.");
        } catch (ResourceNotFoundException e) {
            logger.error("Item not found for soft deletion: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found for deletion.");
        } catch (Exception e) {
            logger.error("Error soft deleting item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error soft deleting item: " + e.getMessage());
        }
    }

    // Update vendor for an inventory item
    @PutMapping("/{inventoryId}/vendor/{vendorId}")
    public ResponseEntity<?> updateVendor(
            @PathVariable Long inventoryId,
            @PathVariable Long vendorId
    ) {
        try {
            InventoryDto updatedInventory = inventoryService.updateVendor(inventoryId, vendorId);
            return ResponseEntity.ok(updatedInventory);
        } catch (ResourceNotFoundException e) {
            logger.error("Error updating vendor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Inventory or Vendor not found"));
        }
    }
}