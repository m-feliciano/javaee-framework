package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.ListCategoryUseCase;
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
public class ListCategoryService implements ListCategoryUseCase {
    @Inject
    private CategoryMapper mapper;
    @Inject
    private CategoryRepositoryPort repository;
    @Inject
    private AuthenticationPort auth;

    @Override
    public Collection<CategoryResponse> list(CategoryRequest request, String token) {
        log.debug("ListCategoryUseCase called with request: {} and token: {}", request, token);

        User user = auth.extractUser(token);
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
        var categories = repository.findAll(new Category(user));
        if (CollectionUtils.isEmpty(categories)) {
            return List.of();
        }

        return categories.stream().map(mapper::toResponse).toList();
    }
}
