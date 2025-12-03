package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.UpdateProductPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class UpdateProductUseCase implements UpdateProductPort {
    @Inject
    private ProductRepositoryPort productRepositoryPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public ProductResponse update(ProductRequest request, String auth) throws ApplicationException {
        log.debug("UpdateProductUseCase: attempting to update product {}", request.id());

        try {
            Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
            product = productRepositoryPort.find(product)
                    .orElseThrow(() -> new ApplicationException("Product not found"));
            product.setName(request.name());
            product.setDescription(request.description());
            product.setPrice(request.price());
            product.setUrl(request.url());
            product.setCategory(Category.builder().id(request.category().id()).build());
            productRepositoryPort.update(product);

            ProductResponse response = productMapper.toResponse(product);
            auditPort.success("product:update", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure("product:update", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
