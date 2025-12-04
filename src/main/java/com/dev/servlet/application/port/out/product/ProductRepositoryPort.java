package com.dev.servlet.application.port.out.product;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

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

