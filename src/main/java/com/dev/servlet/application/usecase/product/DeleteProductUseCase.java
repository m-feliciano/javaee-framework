package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.DeleteProductUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.persistence.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class DeleteProductUseCase implements DeleteProductUseCasePort {
    @Inject
    private ProductRepository productRepository;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public void delete(ProductRequest request, String auth) throws ApplicationException {
        log.debug("DeleteProductUseCase: deleting product with id {}", request.id());
        try {
            Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
            product = productRepository.find(product)
                    .orElseThrow(() -> new ApplicationException("Product not found"));
            productRepository.delete(product);

            auditPort.success("product:delete", auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditPort.failure("product:delete", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
