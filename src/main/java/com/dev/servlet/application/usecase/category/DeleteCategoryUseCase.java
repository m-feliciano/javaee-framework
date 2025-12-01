package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.category.DeleteCategoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.infrastructure.persistence.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class DeleteCategoryUseCase implements DeleteCategoryUseCasePort {
    private static final String EVENT_NAME = "category:delete";

    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private GetCategoryDetailUseCase getCategoryDetailUseCase;
    @Inject
    private AuditPort auditPort;

    @Override
    public void delete(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("DeleteCategoryUseCase called with request: {} and auth: {}", request, auth);

        try {
            String userId = authenticationPort.extractUserId(auth);
            CategoryResponse response = getCategoryDetailUseCase.get(request, auth);

            Category category = new Category(response.getId());
            category.setUser(new User(userId));
            categoryRepository.delete(category);

            CacheUtils.clear(userId, "categoryCacheKey");
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
