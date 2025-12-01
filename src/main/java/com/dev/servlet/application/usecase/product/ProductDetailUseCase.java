package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.ProductDetailUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.persistence.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ProductDetailUseCase implements ProductDetailUseCasePort {
    @Inject
    private ProductRepository productRepository;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;


    @Override
    public ProductResponse get(ProductRequest request, String auth) throws ApplicationException {
        log.debug("GetProductDetailUseCase: attempting to get product detail for product {}", request.id());

        try {
            Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
            product.setStatus(Status.ACTIVE.getValue());
            product = productRepository.find(product)
                    .orElseThrow(() -> new ApplicationException("Product not found"));

            ProductResponse response = productMapper.toResponse(product);
            auditPort.success("product:find_by_id", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure("product:find_by_id", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
