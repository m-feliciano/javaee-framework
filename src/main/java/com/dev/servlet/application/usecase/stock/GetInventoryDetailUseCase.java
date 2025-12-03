package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.GetInventoryDetailPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class GetInventoryDetailUseCase implements GetInventoryDetailPort {
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public InventoryResponse get(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("GetInventoryDetailUseCase: attempting to get inventory detail with id {}", request.id());

        return repositoryPort.findById(request.id())
                .map(inventoryMapper::toResponse)
                .orElseThrow(() -> new ApplicationException("Inventory not found"));
    }
}
