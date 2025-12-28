package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.ProductDetailUserCase;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ProductDetailService implements ProductDetailUserCase {
    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private ProductMapper mapper;
    @Inject
    private AuthenticationPort auth;

    @Override
    public ProductResponse get(ProductRequest request, String auth) throws AppException {
        log.debug("GetProductDetailUseCase: attempting to get product detail for product {}", request.id());

        Product product = mapper.toProduct(request, this.auth.extractUserId(auth));
        product = repository.find(product).orElseThrow(NotFoundException::new);
        return mapper.toResponse(product);
    }
}
