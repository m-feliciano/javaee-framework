package com.servletstack.application.port.out.product;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

public interface ListProductPort {
    <R> IPageable<R> getAllPageable(IPageRequest pageRequest, String auth, Mapper<Product, R> mapper);
}