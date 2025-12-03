package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.port.in.product.ProductCalculatePricePort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.repository.ProductRepository;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class ProductCalculatePriceUseCase implements ProductCalculatePricePort {
    private final ProductRepositoryPort productRepositoryPort;

    @Inject
    public ProductCalculatePriceUseCase(ProductRepository productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product) {
        return productRepositoryPort.calculateTotalPriceFor(product);
    }

}

