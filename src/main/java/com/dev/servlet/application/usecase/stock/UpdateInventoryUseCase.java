package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.UpdateInventoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.persistence.repository.InventoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class UpdateInventoryUseCase implements UpdateInventoryUseCasePort {
    public static final String EVENT_NAME = "inventory:update";

    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private InventoryRepository inventoryRepository;

    @Override
    public InventoryResponse update(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("UpdateInventoryUseCase: attempting to update inventory with id {}", request.id());

        try {
            Inventory inventory = inventoryRepository.findById(request.id())
                    .orElseThrow(() -> new ApplicationException("Inventory not found"));
            inventory.setDescription(request.description());
            inventory.setQuantity(request.quantity());
            inventory.setStatus(Status.ACTIVE.getValue());
            inventoryRepository.update(inventory);

            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditPort.success(EVENT_NAME, auth, response);
            return response;

        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, null);
            throw e;
        }
    }
}
