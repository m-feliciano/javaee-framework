package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

public interface ListProductUseCasePort {
    <U> IPageable<U> getAllPageable(IPageRequest pageRequest, String auth, Mapper<Product, U> mapper);
}