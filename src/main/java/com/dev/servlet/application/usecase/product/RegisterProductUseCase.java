package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.RegisterProductPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@ApplicationScoped
public class RegisterProductUseCase implements RegisterProductPort {
    @Inject
    private ProductRepositoryPort repositoryPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;

    @Override
    public ProductResponse register(ProductRequest request, String auth) throws AppException {
        Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
        product.setRegisterDate(LocalDate.now());
        product.setStatus(Status.ACTIVE.getValue());
        product = repositoryPort.save(product);
        return new ProductResponse(product.getId());
    }
}
