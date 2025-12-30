package com.servletstack.adapter.out.product;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.application.port.out.product.ListProductPort;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ListProductAdapter implements ListProductPort {
    @Inject
    private ProductRepositoryPort repository;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest payload, String auth, Mapper<Product, U> mapper) {
        log.debug("ListProductAdapter: fetching products pageable with payload {}", payload);
        return repository.getAllPageable(payload, mapper);
    }
}
