package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.product.ProductDetailPort;
import com.dev.servlet.application.port.in.stock.RegisterInventoryPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RegisterInventoryUseCase implements RegisterInventoryPort {
    private static final String EVENT_NAME = "inventory:create";
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private InventoryRepositoryPort repositoryPort;
    @Inject
    private ProductDetailPort productDetailPort;

    @Override
    public InventoryResponse register(InventoryCreateRequest request, String auth) throws ApplicationException {
        log.debug("RegisterInventoryUseCase: attempting to register inventory for product {}", request.productId());

        try {
            Inventory inventory = inventoryMapper.createToInventory(request);
            ProductResponse product = productDetailPort.get(
                    new ProductRequest(inventory.getProduct().getId()),
                    auth);
            inventory.setProduct(new Product(product.getId()));
            inventory.setStatus(Status.ACTIVE.getValue());
            inventory.setUser(authenticationPort.extractUser(auth));
            inventory = repositoryPort.save(inventory);

            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
