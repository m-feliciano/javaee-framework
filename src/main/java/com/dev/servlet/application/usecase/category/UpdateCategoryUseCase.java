package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.in.category.UpdateCategoryPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.infrastructure.persistence.repository.CategoryRepository;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class UpdateCategoryUseCase implements UpdateCategoryPort {
    private static final String EVENT_NAME = "category:update";
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;
    @Inject
    private GetCategoryDetailPort categoryDetailPort;
    @Inject
    private CachePort cachePort;

    @Override
    public CategoryResponse update(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("UpdateCategoryUseCase called with request: {} and auth: {}", request, auth);

        try {
            CategoryResponse response = categoryDetailPort.get(request, auth);
            response.setName(request.name().toUpperCase());
            categoryRepository.updateName(new Category(response.getId(), response.getName()));

            cachePort.clear(authenticationPort.extractUserId(auth), "categoryCacheKey");
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
