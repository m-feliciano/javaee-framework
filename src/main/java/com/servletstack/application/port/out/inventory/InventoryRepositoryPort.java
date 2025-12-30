package com.servletstack.application.port.out.inventory;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

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

