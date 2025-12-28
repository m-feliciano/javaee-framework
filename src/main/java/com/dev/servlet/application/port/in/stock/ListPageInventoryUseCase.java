package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

public interface ListPageInventoryUseCase {
    <R> IPageable<R> getAllPageable(IPageRequest pageRequest, String auth, Mapper<Inventory, R> mapper);
}

