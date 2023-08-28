package com.bronx.inventoryservice.mapper;


import com.bronx.inventoryservice.dto.InventoryDto;
import com.bronx.inventoryservice.model.Inventory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class InventoryMapper {

    public InventoryDto fromInventory(Inventory inventory){
        InventoryDto inventoryDto = new InventoryDto();
        BeanUtils.copyProperties(inventory,inventoryDto);
        return inventoryDto;
    }

    public Inventory fromInventoryDto(InventoryDto inventoryDto){
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(inventoryDto,inventory);
        return inventory;
    }
}
