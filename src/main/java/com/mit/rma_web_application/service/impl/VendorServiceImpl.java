package com.mit.rma_web_application.service.impl;

import com.mit.rma_web_application.dto.VendorDTO;
import com.mit.rma_web_application.exception.ResourceNotFoundException;
import com.mit.rma_web_application.model.Vendor;
import com.mit.rma_web_application.repository.VendorRepository;
import com.mit.rma_web_application.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorServiceImpl implements VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);
    private final VendorRepository vendorRepository;

    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public List<VendorDTO> getAllVendors() {
        return vendorRepository.findByDeletedAtIsNull().stream()
                .map(vendor -> new VendorDTO(vendor.getId(), vendor.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public VendorDTO addVendor(VendorDTO vendorDTO) {
        Vendor vendor = new Vendor();
        vendor.setName(vendorDTO.getName());

        Vendor savedVendor = vendorRepository.save(vendor);
        return new VendorDTO(savedVendor.getId(), savedVendor.getName());
    }

    // ✅ FIX: Added getVendorById method
    @Override
    public VendorDTO getVendorById(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        return new VendorDTO(vendor.getId(), vendor.getName());
    }

    @Override
    @Transactional
    public void deleteVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        vendor.setDeletedAt(LocalDateTime.now());
        vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public void restoreVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        vendor.setDeletedAt(null);
        vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public VendorDTO updateVendor(Long vendorId, VendorDTO vendorDTO) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        logger.info("Before Update: Vendor ID = {}, Vendor Name = {}", vendorId, vendor.getName());

        vendor.setName(vendorDTO.getName());

        Vendor updatedVendor = vendorRepository.save(vendor);

        logger.info("After Update: Vendor ID = {}, Vendor Name = {}", updatedVendor.getId(), updatedVendor.getName());

        return new VendorDTO(updatedVendor.getId(), updatedVendor.getName());
    }
}
