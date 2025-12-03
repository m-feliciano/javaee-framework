package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.ListInventoryPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ListInventoryUseCase implements ListInventoryPort {
    private static final String EVENT_NAME = "inventory:list";

    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public List<InventoryResponse> list(InventoryRequest request, String auth) throws ApplicationException {
        log.debug("ListInventoryUseCase: attempting to list inventories with filter {}", request);

        try {
            Inventory inventory = inventoryMapper.toInventory(request);
            User user = User.builder().id(authPort.extractUserId(auth)).build();
            inventory.setUser(user);
            List<Inventory> inventories = repositoryPort.findAll(inventory);
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
