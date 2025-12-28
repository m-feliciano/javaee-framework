package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.stock.ListPageInventoryUseCase;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ListPageInventoryService implements ListPageInventoryUseCase {
    @Inject
    private InventoryRepositoryPort repository;
    @Inject
    private AuthenticationPort authentication;

    @Override
    public <R> IPageable<R> getAllPageable(IPageRequest pageRequest,
                                           String auth,
                                           Mapper<Inventory, R> mapper) {
        log.debug("ListCategoryUseCase called with page initial: {} and size: {}",
                pageRequest.getInitialPage(), pageRequest.getPageSize());

        User user = authentication.extractUser(auth);
        Inventory inventory = ((Inventory) pageRequest.getFilter()).toBuilder().user(user).build();
        pageRequest.setFilter(inventory);
        return repository.getAllPageable(pageRequest, mapper);
    }
}
