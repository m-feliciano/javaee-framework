package com.servletstack.application.port.in.stock;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

public interface ListPageInventoryUseCase {
    <R> IPageable<R> getAllPageable(IPageRequest pageRequest, String auth, Mapper<Inventory, R> mapper);
}

