package com.bronx.orderservice.client;

import com.bronx.orderservice.dto.updateStockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/inventory/{skuCode}/{stock}")
    Boolean checkStock(@PathVariable String skuCode,@PathVariable Integer stock);

    @PostMapping("/api/inventory/update-stock")
    Boolean updateInventory(@RequestBody updateStockDto updateStockDto);
}
