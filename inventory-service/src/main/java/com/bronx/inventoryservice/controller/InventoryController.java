package com.bronx.inventoryservice.controller;

import com.bronx.inventoryservice.dto.InventoryDto;
import com.bronx.inventoryservice.dto.updateStockDto;
import com.bronx.inventoryservice.model.Inventory;
import com.bronx.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    @GetMapping("/{skuCode}/{stock}")
    Boolean isInStock(@PathVariable String skuCode,@PathVariable Integer stock){
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode).orElseThrow(() -> new RuntimeException("Cannot find product by sku code " + skuCode));
        return inventory.getStock() > 0 && inventory.getStock() >= stock;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createInventory(@RequestBody Inventory inventory){
        inventoryRepository.save(inventory);
    }

    @PostMapping("update-stock")
    @ResponseStatus(HttpStatus.ACCEPTED)
    Boolean updateInventory(@RequestBody updateStockDto updateStockDto){
        Inventory inventory = inventoryRepository.findBySkuCode(updateStockDto.getSkuCode()).orElseThrow(() -> new EntityNotFoundException("Cannot find product by sku code "+ updateStockDto.getSkuCode()));
        inventory.setStock(inventory.getStock() - updateStockDto.getQuantity());  ;
        inventoryRepository.save(inventory);
        return true;
    }


}
