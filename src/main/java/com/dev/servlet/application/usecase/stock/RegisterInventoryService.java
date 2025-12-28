package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.product.ProductDetailUserCase;
import com.dev.servlet.application.port.in.stock.RegisterInventoryUseCase;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class RegisterInventoryService implements RegisterInventoryUseCase {
    @Inject
    private InventoryMapper mapper;
    @Inject
    private AuthenticationPort auth;
    @Inject
    private InventoryRepositoryPort repository;
    @Inject
    private ProductDetailUserCase productUserCase;

    @Override
    public InventoryResponse register(InventoryCreateRequest request, String auth) throws AppException {
        log.debug("RegisterInventoryUseCase: attempting to register inventory for product {}", request.productId());

        User user = this.auth.extractUser(auth);

        Inventory inventory = mapper.createToInventory(request);
        ProductResponse product = productUserCase.get(new ProductRequest(inventory.getProduct().getId()), auth);
        inventory.setProduct(new Product(product.getId()));
        inventory.setStatus(Status.ACTIVE.getValue());
        inventory.setUser(user);
        inventory = repository.save(inventory);

        return new InventoryResponse(inventory.getId());
    }
}
