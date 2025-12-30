package com.servletstack.application.usecase.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.mapper.ProductMapper;
import com.servletstack.application.port.in.product.RegisterProductUseCase;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@ApplicationScoped
public class RegisterProductService implements RegisterProductUseCase {
    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private ProductMapper mapper;
    @Inject
    private AuthenticationPort auth;

    @Override
    public ProductResponse register(ProductRequest request, String auth) throws AppException {
        Product product = mapper.toProduct(request, this.auth.extractUserId(auth));
        product.setRegisterDate(LocalDate.now());
        product.setStatus(Status.ACTIVE.getValue());
        product = repository.save(product);
        return new ProductResponse(product.getId());
    }
}
