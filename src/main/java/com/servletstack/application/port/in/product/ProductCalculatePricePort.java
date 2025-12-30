package com.servletstack.application.port.in.product;

import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

import java.math.BigDecimal;

public interface ProductCalculatePricePort {
    BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product);
}
