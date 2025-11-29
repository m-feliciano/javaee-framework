package com.dev.servlet.application.port.out.repository;

import com.dev.servlet.domain.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Optional<Inventory> find(Inventory inventory);

    List<Inventory> findAll(Inventory inventory);

    void delete(Inventory inventory);

    List<Inventory> save(List<Inventory> inventories);

    boolean has(Inventory inventory);
}

