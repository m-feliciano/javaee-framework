package com.dev.servlet.application.port.out.inventory;

import com.dev.servlet.domain.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Optional<Inventory> find(Inventory inventory);

    List<Inventory> findAll(Inventory inventory);

    void delete(Inventory inventory);

    List<Inventory> saveAll(List<Inventory> inventories);

    boolean has(Inventory inventory);

    Optional<Inventory> findById(String id);

    Inventory save(Inventory inventory);

    Inventory update(Inventory inventory);
}

