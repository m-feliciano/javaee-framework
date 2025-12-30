package com.servletstack.adapter.out.inventory;

import com.servletstack.application.port.in.stock.HasInventoryUseCase;
import com.servletstack.application.port.out.inventory.InventoryRepositoryPort;
import com.servletstack.domain.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HasInventoryAdapter implements HasInventoryUseCase {
    @Inject
    private InventoryRepositoryPort repository;

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        log.debug("HasInventoryUseCase: checking inventory existence for item {}", inventory.getId());
        return repository.has(inventory);
    }
}
