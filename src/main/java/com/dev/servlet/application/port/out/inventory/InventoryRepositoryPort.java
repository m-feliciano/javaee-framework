package com.dev.servlet.application.port.out.inventory;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepositoryPort {
    Optional<Inventory> find(Inventory inventory);

    List<Inventory> findAll(Inventory inventory);

    void delete(Inventory inventory);

    boolean has(Inventory inventory);

    Optional<Inventory> findById(UUID id);

    Inventory save(Inventory inventory);

    Inventory update(Inventory inventory);

    <U> IPageable<U> getAllPageable(IPageRequest payload, Mapper<Inventory, U> mapper);
}

