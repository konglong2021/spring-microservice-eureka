package com.bronx.inventoryservice.service.Impl;

import com.bronx.inventoryservice.client.ProductClient;
import com.bronx.inventoryservice.dto.InventoryDto;
import com.bronx.inventoryservice.mapper.InventoryMapper;
import com.bronx.inventoryservice.model.Inventory;
import com.bronx.inventoryservice.repository.InventoryRepository;
import com.bronx.inventoryservice.service.InventoryService;

public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    public InventoryServiceImpl(InventoryMapper inventoryMapper, InventoryRepository inventoryRepository, ProductClient productClient) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryRepository = inventoryRepository;
        this.productClient = productClient;
    }

    public void updateInventory(InventoryDto inventoryDto){
        Inventory inventory = inventoryMapper.fromInventoryDto(inventoryDto);
        inventoryRepository.save(inventory);
    }

    public Inventory createInventory(InventoryDto inventoryDto){
        if (productClient.checkProductExisted(inventoryDto.getSkuCode())){
            Inventory inventory = inventoryMapper.fromInventoryDto(inventoryDto);
            return inventoryRepository.save(inventory);
        }
            return null;
    }
}
