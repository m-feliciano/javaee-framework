package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.product.ProductDetailUseCasePort;
import com.dev.servlet.application.port.in.stock.RegisterInventoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.persistence.repository.InventoryRepository;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RegisterInventoryUseCase implements RegisterInventoryUseCasePort {
    private static final String EVENT_NAME = "inventory:create";
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private InventoryRepository inventoryRepository;
    @Inject
    private ProductDetailUseCasePort productDetailUseCasePort;

    @Override
    public InventoryResponse register(InventoryCreateRequest request, String auth) throws ApplicationException {
        log.debug("RegisterInventoryUseCase: attempting to register inventory for product {}", request.productId());

        try {
            Inventory inventory = inventoryMapper.createToInventory(request);
            ProductResponse product = productDetailUseCasePort.get(
                    new ProductRequest(inventory.getProduct().getId()),
                    auth);
            inventory.setProduct(new Product(product.getId()));
            inventory.setStatus(Status.ACTIVE.getValue());
            inventory.setUser(authenticationPort.extractUser(auth));
            inventory = inventoryRepository.save(inventory);

            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
