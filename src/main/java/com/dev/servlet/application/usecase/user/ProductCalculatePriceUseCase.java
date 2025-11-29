package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.port.in.product.ProductCalculatePriceUseCasePort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.repository.ProductRepository;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class ProductCalculatePriceUseCase implements ProductCalculatePriceUseCasePort {
    private final ProductRepository productRepository;

    @Inject
    public ProductCalculatePriceUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product) {
        return productRepository.calculateTotalPriceFor(product);
    }

}

