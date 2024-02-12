package com.bronx.inventoryservice.service;


import com.bronx.inventoryservice.dto.InventoryDto;
import com.bronx.inventoryservice.model.Inventory;

public interface InventoryService {

    void updateInventory(InventoryDto inventoryDto);
    Inventory createInventory(InventoryDto inventoryDto);
}
