package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.UpdateProductPort;
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
public class UpdateProductUseCase implements UpdateProductPort {
    @Inject
    private ProductRepositoryPort productRepositoryPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;

    @Override
    public ProductResponse update(ProductRequest request, String auth) throws ApplicationException {
        log.debug("UpdateProductUseCase: attempting to update product {}", request.id());

        Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
        product = productRepositoryPort.find(product).orElseThrow(() -> new ApplicationException("Product not found"));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setUrl(request.url());
        product.setCategory(Category.builder().id(request.category().id()).build());
        productRepositoryPort.update(product);
        return productMapper.toResponse(product);
    }
}
