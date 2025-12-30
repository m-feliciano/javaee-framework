package com.servletstack.application.port.in.stock;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

public interface ListInventoryUseCase {
    <U> IPageable<U> getAllPageable(IPageRequest pageRequest, String auth, Mapper<Inventory, U> mapper);
}

