package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.RegisterCategoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
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
public class RegisterCategoryUseCase implements RegisterCategoryUseCasePort {
    private static final String EVENT_NAME = "category:register";

    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public CategoryResponse register(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("RegisterCategoryUseCase called with request: {} and auth: {}", request, auth);

        try {
            User user = authenticationPort.extractUser(auth);
            Category category = categoryMapper.toCategory(request);
            category.setUser(user);
            category.setStatus(Status.ACTIVE.getValue());
            category = categoryRepository.save(category);
            CacheUtils.clear(user.getId(), "categoryCacheKey");

            CategoryResponse response = categoryMapper.toResponse(category);
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }
}
