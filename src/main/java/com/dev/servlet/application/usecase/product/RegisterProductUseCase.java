package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.RegisterProductPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RegisterProductUseCase implements RegisterProductPort {
    @Inject
    private ProductRepositoryPort repositoryPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public ProductResponse register(ProductRequest request, String auth) throws ApplicationException {
        try {
            Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
            product.setRegisterDate(LocalDate.now());
            product.setStatus(Status.ACTIVE.getValue());
            product = repositoryPort.save(product);
            ProductResponse response = productMapper.toResponse(product);
            auditPort.success("product:create", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure("product:create", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
