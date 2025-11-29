package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.ListCategoryUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.repository.CategoryRepository;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.shared.util.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ListCategoryUseCase implements ListCategoryUseCasePort {
    private static final String EVENT_NAME = "category:list";

    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public Collection<CategoryResponse> list(CategoryRequest request, String token) {
        log.debug("ListCategoryUseCase called with request: {} and token: {}", request, token);

        try {
            User user = authenticationPort.extractUser(token);
            Collection<CategoryResponse> categories = getAll(user);
            if (request != null && request.name() != null) {
                String lowerCase = request.name().toLowerCase();
                categories = categories.stream()
                        .filter(c -> c.getName().toLowerCase().contains(lowerCase))
                        .toList();
            }

            auditPort.success(EVENT_NAME, token, new AuditPayload<>(request, categories));
            return categories;
        } catch (Exception e) {
            auditPort.failure(EVENT_NAME, token, new AuditPayload<>(request, null));
            throw e;
        }
    }

    private Collection<CategoryResponse> getAll(User user) {
        final String userId = user.getId();
        List<CategoryResponse> response = CacheUtils.get(userId, "categoryCacheKey");
        if (CollectionUtils.isEmpty(response)) {
            var categories = categoryRepository.findAll(new Category(user));
            if (!CollectionUtils.isEmpty(categories)) {
                response = categories.stream().map(categoryMapper::toResponse).toList();
                CacheUtils.set(userId, "categoryCacheKey", response);
            }
        }
        return response;
    }
}
