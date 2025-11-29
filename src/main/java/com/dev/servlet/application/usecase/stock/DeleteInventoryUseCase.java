package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.stock.DeleteInventoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.infrastructure.persistence.repository.InventoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class DeleteInventoryUseCase implements DeleteInventoryUseCasePort {
    @Inject
    private AuditPort auditPort;
    @Inject
    private InventoryRepository inventoryRepository;

    @Override
    public void delete(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("DeleteInventoryUseCase: attempting to delete inventory with id {}", request.id());

        try {
            Inventory inventory = inventoryRepository.findById(request.id())
                    .orElseThrow(() -> new ApplicationException("Inventory not found"));
            inventoryRepository.delete(inventory);
            auditPort.success("inventory:delete", auth, null);
        } catch (Exception e) {
            auditPort.failure("inventory:delete", auth, null);
            throw e;
        }
    }
}
