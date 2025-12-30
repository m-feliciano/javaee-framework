package com.servletstack.application.usecase.stock;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.application.port.in.stock.ListPageInventoryUseCase;
import com.servletstack.application.port.out.inventory.InventoryRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.domain.entity.User;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
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
