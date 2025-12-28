package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.RegisterCategoryUseCase;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class RegisterCategoryService implements RegisterCategoryUseCase {
    @Inject
    private CategoryMapper mapper;
    @Inject
    private CategoryRepositoryPort repository;
    @Inject
    private AuthenticationPort auth;

    @Override
    public CategoryResponse register(CategoryRequest request, String auth) throws AppException {
        log.debug("RegisterCategoryUseCase called with request: {} and auth: {}", request, auth);

        User user = this.auth.extractUser(auth);
        Category category = mapper.toCategory(request);
        category.setUser(user);
        category.setStatus(Status.ACTIVE.getValue());
        category = repository.save(category);
        return CategoryResponse.builder().id(category.getId()).build();
    }
}
