package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.UpdateInventoryPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UpdateInventoryUseCase implements UpdateInventoryPort {
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public InventoryResponse update(InventoryRequest request, String auth) throws AppException {
        log.debug("UpdateInventoryUseCase: attempting to update inventory with id {}", request.id());

        Inventory inventory = repositoryPort.findById(request.id()).orElseThrow(() -> new AppException("Inventory not found"));
        inventory.setDescription(request.description());
        inventory.setQuantity(request.quantity());
        inventory.setStatus(Status.ACTIVE.getValue());
        repositoryPort.update(inventory);
        return new InventoryResponse(inventory.getId());
    }
}
