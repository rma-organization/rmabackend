package com.mit.rma_web_application.services;

import java.util.List;

import com.mit.rma_web_application.dtos.VendorDTO;

public interface VendorService {
    List<VendorDTO> getAllVendors();
    VendorDTO addVendor(VendorDTO vendorDTO);
    void deleteVendor(Long vendorId);
    void restoreVendor(Long vendorId);

    // The correct method signature
    VendorDTO updateVendor(Long vendorId, VendorDTO vendorDTO);  // <- Method signature should match
}
