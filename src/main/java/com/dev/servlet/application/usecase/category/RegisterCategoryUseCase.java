package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.RegisterCategoryPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RegisterCategoryUseCase implements RegisterCategoryPort {
    private static final String EVENT_NAME = "category:register";
    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepositoryPort categoryRepositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;
    @Inject
    private CachePort cachePort;

    @Override
    public CategoryResponse register(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("RegisterCategoryUseCase called with request: {} and auth: {}", request, auth);

        try {
            User user = authenticationPort.extractUser(auth);
            Category category = categoryMapper.toCategory(request);
            category.setUser(user);
            category.setStatus(Status.ACTIVE.getValue());
            category = categoryRepositoryPort.save(category);
            cachePort.clear(user.getId(), "categoryCacheKey");

            CategoryResponse response = categoryMapper.toResponse(category);
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
