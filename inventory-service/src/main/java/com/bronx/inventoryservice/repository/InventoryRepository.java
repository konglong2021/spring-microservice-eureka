package com.bronx.inventoryservice.repository;

import com.bronx.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

   Optional<Inventory> findBySkuCode(String skuCode);
}
