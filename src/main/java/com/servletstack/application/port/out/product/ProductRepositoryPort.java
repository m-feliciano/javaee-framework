package com.servletstack.application.port.out.product;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    BigDecimal calculateTotalPriceFor(Product filter);

    Optional<Product> find(Product product);

    Product update(Product product);

    List<Product> saveAll(List<Product> products);

    <U> IPageable<U> getAllPageable(IPageRequest payload, Mapper<Product, U> mapper);

    Product save(Product product);

    void delete(Product product);
}

