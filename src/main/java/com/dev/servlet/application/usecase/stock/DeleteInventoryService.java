package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.port.in.stock.DeleteInventoryUseCase;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.domain.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class DeleteInventoryService implements DeleteInventoryUseCase {
    @Inject
    private InventoryRepositoryPort repository;

    @Override
    public void delete(UUID uuid) throws AppException {
        Inventory inventory = repository.findById(uuid).orElseThrow(NotFoundException::new);
        repository.delete(inventory);
    }
}
