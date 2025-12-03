package com.dev.servlet.application.port.out.product;

import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepositoryPort;

import java.math.BigDecimal;

public interface ProductRepositoryPort extends BaseRepositoryPort<Product, String> {
    BigDecimal calculateTotalPriceFor(Product filter);
}

