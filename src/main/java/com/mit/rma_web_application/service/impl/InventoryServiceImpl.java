package com.mit.rma_web_application.service.impl;

import com.mit.rma_web_application.dtos.InventoryDto;
import com.mit.rma_web_application.exception.ResourceNotFoundException;
import com.mit.rma_web_application.mapper.InventoryMapper;
import com.mit.rma_web_application.model.Inventory;
import com.mit.rma_web_application.model.Vendor;
import com.mit.rma_web_application.repository.InventoryRepository;
import com.mit.rma_web_application.repository.VendorRepository;
import com.mit.rma_web_application.service.InventoryService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final InventoryRepository inventoryRepository;
    private final VendorRepository vendorRepository;

    @Override
    public InventoryDto createInventory(InventoryDto inventoryDto) {
        logger.info("Creating inventory with name: {}", inventoryDto.getName());

        Vendor vendor = vendorRepository.findById(inventoryDto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        Inventory inventory = InventoryMapper.mapToInventory(inventoryDto, vendor);
        Inventory savedInventory = inventoryRepository.save(inventory);
        logger.info("Created inventory item with ID: {}", savedInventory.getId());

        return InventoryMapper.mapToInventoryDto(savedInventory);
    }

    @Override
    public InventoryDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        logger.info("Fetched inventory item with ID: {}", id);
        return InventoryMapper.mapToInventoryDto(inventory);
    }

    @Override
    public List<InventoryDto> getAllInventory(int page, int size) {
        List<Inventory> inventoryList = inventoryRepository.findAll().stream()
                .filter(inventory -> !inventory.isDeleted()) // Exclude soft-deleted items
                .collect(Collectors.toList());

        return inventoryList.stream()
                .map(InventoryMapper::mapToInventoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto updateInventory(Long id, InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventory.setName(inventoryDto.getName());
        inventory.setQuantity(inventoryDto.getQuantity());
        inventory.setMitNumber(inventoryDto.getMitNumber());
        inventory.setDescription(inventoryDto.getDescription());
        inventory.setInventoryLocation(inventoryDto.getInventoryLocation());
        inventory.setItemType(inventoryDto.getItemType());
        inventory.setPoNumber(inventoryDto.getPoNumber());
        inventory.setLotNumber(inventoryDto.getLotNumber());
        inventory.setInBoxPartNumber(inventoryDto.getInBoxPartNumber());
        inventory.setBoxPartNumber(inventoryDto.getBoxPartNumber());
        inventory.setInBoxSerialNumber(inventoryDto.getInBoxSerialNumber());
        inventory.setBoxSerialNumber(inventoryDto.getBoxSerialNumber());
        inventory.setStatus(inventoryDto.getStatus());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        logger.info("Updated inventory item with ID: {}", updatedInventory.getId());

        return InventoryMapper.mapToInventoryDto(updatedInventory);
    }

    @Override
    public void softDeleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventory.softDelete();
        inventoryRepository.save(inventory);
        logger.info("Soft deleted inventory item with ID: {}", id);
    }

    @Override
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventoryRepository.delete(inventory);
        logger.info("Deleted inventory item with ID: {}", id);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public InventoryDto updateVendor(Long inventoryId, Long vendorId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        inventory.setVendor(vendor);

        entityManager.detach(inventory);  // Ensure it's treated as a new transaction
        Inventory updatedInventory = inventoryRepository.save(inventory);
        entityManager.flush();

        return InventoryMapper.mapToInventoryDto(updatedInventory);
    }

}