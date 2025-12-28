package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.UpdateProductUseCase;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UpdateProductService implements UpdateProductUseCase {
    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private ProductMapper mapper;
    @Inject
    private AuthenticationPort auth;

    @Override
    public ProductResponse update(ProductRequest request, String auth) throws AppException {
        log.debug("UpdateProductUseCase: attempting to update product {}", request.id());

        Product product = mapper.toProduct(request, this.auth.extractUserId(auth));
        product = repository.find(product).orElseThrow(NotFoundException::new);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());

        product.setCategory(
                Category.builder()
                        .id(request.category().id())
                        .build()
        );
        repository.update(product);
        return new ProductResponse(product.getId());
    }
}
