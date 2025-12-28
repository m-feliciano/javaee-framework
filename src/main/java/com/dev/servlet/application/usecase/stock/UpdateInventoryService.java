package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.stock.UpdateInventoryUseCase;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UpdateInventoryService implements UpdateInventoryUseCase {
    @Inject
    private InventoryRepositoryPort repository;

    @Override
    public InventoryResponse update(InventoryRequest request, String auth) throws AppException {
        log.debug("UpdateInventoryUseCase: attempting to update inventory with id {}", request.id());

        Inventory inventory = repository.findById(request.id()).orElseThrow(() -> new AppException("Inventory not found"));
        inventory.setDescription(request.description());
        inventory.setQuantity(request.quantity());
        repository.update(inventory);
        return new InventoryResponse(inventory.getId());
    }
}
