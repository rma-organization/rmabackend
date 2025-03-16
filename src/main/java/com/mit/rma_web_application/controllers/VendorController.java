package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.VendorDTO;
import com.mit.rma_web_application.services.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    //  Get all vendors
    @GetMapping
    public ResponseEntity<List<VendorDTO>> getAllVendors() {
        List<VendorDTO> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    //  Add a new vendor
    @PostMapping
    public ResponseEntity<VendorDTO> addVendor(@RequestBody VendorDTO vendorDTO) {
        VendorDTO createdVendor = vendorService.addVendor(vendorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVendor);
    }

    //  Update a vendor (Fix for your PUT request issue)
    @PutMapping("/{vendorId}")
    public ResponseEntity<VendorDTO> updateVendor(@PathVariable Long vendorId, @RequestBody VendorDTO vendorDTO) {
        try {
            VendorDTO updatedVendor = vendorService.updateVendor(vendorId, vendorDTO);
            return ResponseEntity.ok(updatedVendor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //  Delete a vendor
    @DeleteMapping("/{vendorId}")
    public ResponseEntity<String> deleteVendor(@PathVariable Long vendorId) {
        try {
            vendorService.deleteVendor(vendorId);
            return ResponseEntity.ok("Vendor deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //  Restore a deleted vendor
    @PatchMapping("/{vendorId}/restore")
    public ResponseEntity<String> restoreVendor(@PathVariable Long vendorId) {
        try {
            vendorService.restoreVendor(vendorId);
            return ResponseEntity.ok("Vendor restored successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
