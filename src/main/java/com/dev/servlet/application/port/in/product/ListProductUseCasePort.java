package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.port.contracts.PageableUseCasePort;
import com.dev.servlet.domain.entity.Product;

public interface ListProductUseCasePort extends PageableUseCasePort<Product> {
}