package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class GetCategoryDetailUseCase implements GetCategoryDetailPort {
    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepositoryPort categoryRepositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;

    @Override
    public CategoryResponse get(CategoryRequest request, String auth) throws AppException {
        log.debug("GetCategoryDetailUseCase called with request: {} and auth: {}", request, auth);

        String userId = authenticationPort.extractUserId(auth);
        return categoryMapper.toResponse(findById(request.id(), userId));
    }

    private Category findById(String entityId, String userId) throws AppException {
        Category category = Category.builder()
                .id(entityId)
                .user(new User(userId))
                .build();
        return categoryRepositoryPort.find(category).orElseThrow(() -> new AppException("Category not found"));
    }
}
