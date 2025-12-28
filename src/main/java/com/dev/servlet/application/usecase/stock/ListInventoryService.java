package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.stock.ListInventoryUseCase;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ListInventoryService implements ListInventoryUseCase {
    @Inject
    private AuthenticationPort auth;
    @Inject
    private InventoryRepositoryPort repository;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest pageRequest,
                                           String auth,
                                           Mapper<Inventory, U> mapper) {
        log.debug("ListInventoryUseCase: attempting to list inventories pageable with initialPage {}, pageSize {}",
                pageRequest.getInitialPage(), pageRequest.getPageSize());

        UUID userId = this.auth.extractUserId(auth);

        Inventory filter = (Inventory) pageRequest.getFilter();
        filter = ObjectUtils.getIfNull(filter, Inventory::new);
        filter.setUser(User.builder().id(userId).build());
        pageRequest.setFilter(filter);

        return repository.getAllPageable(pageRequest, mapper);
    }
}
