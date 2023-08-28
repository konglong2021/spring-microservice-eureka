package com.bronx.inventoryservice.service.Impl;

import com.bronx.inventoryservice.dto.InventoryDto;
import com.bronx.inventoryservice.mapper.InventoryMapper;
import com.bronx.inventoryservice.model.Inventory;
import com.bronx.inventoryservice.repository.InventoryRepository;
import com.bronx.inventoryservice.service.InventoryService;

public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryMapper inventoryMapper, InventoryRepository inventoryRepository) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryRepository = inventoryRepository;
    }

    public void updateInventory(InventoryDto inventoryDto){
        Inventory inventory = inventoryMapper.fromInventoryDto(inventoryDto);
        inventoryRepository.save(inventory);
    }
}
