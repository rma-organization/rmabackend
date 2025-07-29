package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.dtos.InventoryDto;
import com.mit.rma_web_application.exceptions.ResourceNotFoundException;
import com.mit.rma_web_application.mappers.InventoryMapper;
import com.mit.rma_web_application.models.Inventory;
import com.mit.rma_web_application.models.Vendor;
import com.mit.rma_web_application.repositories.InventoryRepository;
import com.mit.rma_web_application.repositories.VendorRepository;
import com.mit.rma_web_application.services.InventoryService;
import com.mit.rma_web_application.services.interfaces.NotificationService;

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
    private final NotificationService notificationService; // Injected NotificationService

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public InventoryDto createInventory(InventoryDto inventoryDto) {
        logger.info("Creating inventory with name: {}", inventoryDto.getName());

        Vendor vendor = vendorRepository.findById(inventoryDto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        Inventory inventory = InventoryMapper.mapToInventory(inventoryDto, vendor);
        Inventory savedInventory = inventoryRepository.save(inventory);
        logger.info("Created inventory item with ID: {}", savedInventory.getId());

        // Send notification to all engineers with 6 arguments (including null requestId)
        notificationService.sendNotification(
                "engineer",
                "New inventory added: " + savedInventory.getName(),
                "inventory",
                "admin",
                null,
                null// requestId is null here, no associated request
        );

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
                .filter(inventory -> !inventory.isDeleted())
                .collect(Collectors.toList());

        return inventoryList.stream()
                .map(InventoryMapper::mapToInventoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto updateInventory(Long id, InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        Vendor vendor = vendorRepository.findById(inventoryDto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

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
        inventory.setAirwayBillNumber(inventoryDto.getAirwaybillnumber());
        inventory.setCurrency(inventoryDto.getCurrency());
        inventory.setAmount(inventoryDto.getAmount());
        inventory.setVendor(vendor);

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

    @Override
    public InventoryDto updateVendor(Long inventoryId, Long vendorId) {
        Inventory inventory = inventoryRepository.findByIdAndDeletedAtIsNull(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        inventory.setVendor(vendor);

        entityManager.detach(inventory);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        entityManager.flush();

        logger.info("Updated vendor for inventory ID: {}", inventoryId);
        return InventoryMapper.mapToInventoryDto(updatedInventory);
    }
}
