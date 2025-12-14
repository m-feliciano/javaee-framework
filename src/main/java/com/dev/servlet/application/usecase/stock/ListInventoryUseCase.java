package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.ListInventoryPort;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

@Slf4j
@ApplicationScoped
public class ListInventoryUseCase implements ListInventoryPort {
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private InventoryRepositoryPort repositoryPort;

    @Override
    public List<InventoryResponse> list(InventoryRequest request, String auth) throws AppException {
        log.debug("ListInventoryUseCase: attempting to list inventories with filter {}", request);
        String userId = authPort.extractUserId(auth);

        Inventory inventory = inventoryMapper.toInventory(request);
        inventory = ObjectUtils.getIfNull(inventory, Inventory::new);
        inventory.setUser(User.builder().id(userId).build());

        List<Inventory> inventories = repositoryPort.findAll(inventory);
        return inventories.stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }
}
