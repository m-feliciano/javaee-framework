package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.port.in.stock.HasInventoryPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class HasInventoryUseCase implements HasInventoryPort {
    private static final String EVENT_NAME = "inventory:has_inventory";

    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        log.debug("HasInventoryUseCase: checking inventory existence for item {}", inventory.getId());

        try {
            inventory.setUser(authenticationPort.extractUser(auth));
            boolean result = repositoryPort.has(inventory);
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(inventory, result));
            return result;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(inventory, null));
            throw e;
        }
    }
}
