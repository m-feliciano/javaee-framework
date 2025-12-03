package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.util.CollectionUtils;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ListCategoryUseCase implements ListCategoryPort {
    private static final String EVENT_NAME = "category:list";

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
    public Collection<CategoryResponse> list(CategoryRequest request, String token) {
        log.debug("ListCategoryUseCase called with request: {} and token: {}", request, token);

        try {
            User user = authenticationPort.extractUser(token);
            Collection<CategoryResponse> categories = findAll(user);
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

    private Collection<CategoryResponse> findAll(User user) {
        final String userId = user.getId();

        List<CategoryResponse> response = cachePort.get(userId, "categoryCacheKey");
        if (CollectionUtils.isEmpty(response)) {
            var categories = categoryRepositoryPort.findAll(new Category(user));
            if (!CollectionUtils.isEmpty(categories)) {
                response = categories.stream().map(categoryMapper::toResponse).toList();
                cachePort.set(userId, "categoryCacheKey", response);
            }
        }

        return response;
    }
}
