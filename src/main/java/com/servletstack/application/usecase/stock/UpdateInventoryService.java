package com.servletstack.application.usecase.stock;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.in.stock.UpdateInventoryUseCase;
import com.servletstack.application.port.out.inventory.InventoryRepositoryPort;
import com.servletstack.application.transfer.request.InventoryRequest;
import com.servletstack.application.transfer.response.InventoryResponse;
import com.servletstack.domain.entity.Inventory;
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
