package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.GetInventoryDetailPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class GetInventoryDetailUseCase implements GetInventoryDetailPort {
    private static final String EVENT_NAME = "inventory:find_by_id";

    @Inject
    private AuditPort auditPort;
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public InventoryResponse get(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("GetInventoryDetailUseCase: attempting to get inventory detail with id {}", request.id());

        try {
            Inventory inventory = repositoryPort.findById(request.id())
                    .orElseThrow(() -> new ApplicationException("Inventory not found"));
            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditPort.success(EVENT_NAME, auth, response);
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, null);
            throw e;
        }
    }
}
