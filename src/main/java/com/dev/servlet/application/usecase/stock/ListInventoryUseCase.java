package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.ListInventoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.infrastructure.persistence.repository.InventoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ListInventoryUseCase implements ListInventoryUseCasePort {
    private static final String EVENT_NAME = "inventory:list";

    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private InventoryRepository inventoryRepository;

    @Override
    public List<InventoryResponse> list(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("ListInventoryUseCase: attempting to list inventories with filter {}", request);

        try {
            Inventory inventory = inventoryMapper.toInventory(request);
            inventory.setUser(authPort.extractUser(auth));
            List<Inventory> inventories = inventoryRepository.findAll(inventory);
            List<InventoryResponse> responses = inventories.stream()
                    .map(inventoryMapper::toResponse)
                    .toList();
            auditPort.success(EVENT_NAME, auth, responses);
            return responses;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, null);
            throw e;
        }
    }
}
