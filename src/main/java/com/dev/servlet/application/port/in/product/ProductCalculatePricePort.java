package com.dev.servlet.application.port.in.product;

import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

import java.math.BigDecimal;

public interface ProductCalculatePricePort {
    BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product);
}
