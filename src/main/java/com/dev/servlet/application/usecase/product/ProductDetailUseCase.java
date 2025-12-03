package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.ProductDetailPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ProductDetailUseCase implements ProductDetailPort {
    @Inject
    private ProductRepositoryPort repositoryPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;

    @Override
    public ProductResponse get(ProductRequest request, String auth) throws ApplicationException {
        log.debug("GetProductDetailUseCase: attempting to get product detail for product {}", request.id());

        Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
        product.setStatus(Status.ACTIVE.getValue());
        product = repositoryPort.find(product).orElseThrow(() -> new ApplicationException("Product not found"));
        return productMapper.toResponse(product);
    }
}
