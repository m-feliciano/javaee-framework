package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.UpdateCategoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.repository.CategoryRepository;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class UpdateCategoryUseCase implements UpdateCategoryUseCasePort {
    private static final String EVENT_NAME = "category:update";

    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public CategoryResponse update(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("UpdateCategoryUseCase called with request: {} and auth: {}", request, auth);

        try {
            String userId = authenticationPort.extractUserId(auth);
            Category category = loadCategory(request.id(), userId);
            category.setName(request.name().toUpperCase());
            categoryRepository.update(category);
            CacheUtils.clear(userId, "categoryCacheKey");
            CategoryResponse response = categoryMapper.toResponse(category);
            auditPort.success(EVENT_NAME, auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    private Category loadCategory(String categoryId, String userId) throws ApplicationException {
        Category category = Category.builder().id(categoryId).user(new User(userId)).build();
        return categoryRepository.find(category).orElseThrow(() -> new ApplicationException("Category not found"));
    }
}
