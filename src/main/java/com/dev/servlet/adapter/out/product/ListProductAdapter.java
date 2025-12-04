package com.dev.servlet.adapter.out.product;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.out.product.ListProductPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ListProductAdapter implements ListProductPort {
    @Inject
    private ProductRepositoryPort repositoryPort;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest payload, String auth, Mapper<Product, U> mapper) {
        log.debug("ListProductAdapter: fetching products pageable with payload {}", payload);
        return repositoryPort.getAllPageable(payload, mapper);
    }
}
