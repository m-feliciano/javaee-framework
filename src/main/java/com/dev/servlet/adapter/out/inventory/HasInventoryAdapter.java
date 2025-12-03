package com.dev.servlet.adapter.out.inventory;

import com.dev.servlet.application.port.in.stock.HasInventoryPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.domain.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HasInventoryAdapter implements HasInventoryPort {
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        log.debug("HasInventoryUseCase: checking inventory existence for item {}", inventory.getId());
        return repositoryPort.has(inventory);
    }
}
