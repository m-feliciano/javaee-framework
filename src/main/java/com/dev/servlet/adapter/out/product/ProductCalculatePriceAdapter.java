package com.dev.servlet.adapter.out.product;

import com.dev.servlet.application.port.in.product.ProductCalculatePricePort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class ProductCalculatePriceAdapter implements ProductCalculatePricePort {
    @Inject
    private ProductRepositoryPort repository;

    @Override
    public BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product) {
        return repository.calculateTotalPriceFor(product);
    }

}

