package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.util.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ListCategoryUseCase implements ListCategoryPort {
    private static final String CACHE_NAMESPACE = "categoryCacheKey";

    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepositoryPort categoryRepositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private CachePort cachePort;

    @Override
    public Collection<CategoryResponse> list(CategoryRequest request, String token) {
        log.debug("ListCategoryUseCase called with request: {} and token: {}", request, token);

        User user = authenticationPort.extractUser(token);
        Collection<CategoryResponse> categories = findAll(user);
        if (request != null && request.name() != null) {
            String lowerCase = request.name().toLowerCase();
            categories = categories.stream()
                    .filter(c -> c.getName().toLowerCase().contains(lowerCase))
                    .toList();
        }
        return categories;
    }

    private Collection<CategoryResponse> findAll(User user) {
        final String userId = user.getId();

        List<CategoryResponse> response = cachePort.get(CACHE_NAMESPACE, userId);
        if (CollectionUtils.isEmpty(response)) {
            var categories = categoryRepositoryPort.findAll(new Category(user));
            if (!CollectionUtils.isEmpty(categories)) {
                response = categories.stream().map(categoryMapper::toResponse).toList();
                cachePort.set(CACHE_NAMESPACE, userId, response);
            }
        }

        return response;
    }
}
