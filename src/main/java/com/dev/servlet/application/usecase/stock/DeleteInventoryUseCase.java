package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.stock.DeleteInventoryPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.domain.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class DeleteInventoryUseCase implements DeleteInventoryPort {
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public void delete(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("DeleteInventoryUseCase: attempting to delete inventory with id {}", request.id());

        Inventory inventory = repositoryPort.findById(request.id())
                .orElseThrow(() -> new ApplicationException("Inventory not found"));
        repositoryPort.delete(inventory);
    }
}
