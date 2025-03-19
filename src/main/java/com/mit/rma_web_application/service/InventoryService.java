package com.mit.rma_web_application.service;

import java.util.List;

import com.mit.rma_web_application.dto.InventoryDto;

public interface InventoryService {

    InventoryDto createInventory(InventoryDto inventoryDto);

    InventoryDto getInventoryById(Long id);

    List<InventoryDto> getAllInventory(int page, int size);

    InventoryDto updateInventory(Long id, InventoryDto inventoryDto);

    void deleteInventory(Long id);

    void softDeleteInventory(Long id);

    InventoryDto updateVendor(Long inventoryId, Long vendorId); // Add this method
}
