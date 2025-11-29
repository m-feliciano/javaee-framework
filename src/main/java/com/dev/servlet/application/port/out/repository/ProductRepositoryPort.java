package com.dev.servlet.application.port.out.repository;

import com.dev.servlet.domain.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Optional<Product> find(Product product);

    List<Product> findAll(Product product);

    void delete(Product product);

    BigDecimal calculateTotalPriceFor(Product filter);
}

