package com.servletstack.application.usecase.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.mapper.ProductMapper;
import com.servletstack.application.port.in.product.ProductDetailUserCase;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Product;
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
