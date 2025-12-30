package com.servletstack.application.usecase.category;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.mapper.CategoryMapper;
import com.servletstack.application.port.in.category.GetCategoryDetailUseCase;
import com.servletstack.application.port.out.category.CategoryRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.domain.entity.Category;
import com.servletstack.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GetCategoryDetailService implements GetCategoryDetailUseCase {
    @Inject
    private CategoryMapper mapper;
    @Inject
    private CategoryRepositoryPort repository;
    @Inject
    private AuthenticationPort auth;

    @Override
    public CategoryResponse get(CategoryRequest request, String auth) throws AppException {
        log.debug("GetCategoryDetailUseCase called with request: {} and auth: {}", request, auth);

        UUID userId = this.auth.extractUserId(auth);
        return mapper.toResponse(findById(request.id(), userId));
    }

    private Category findById(UUID entityId, UUID userId) throws AppException {
        Category category = Category.builder()
                .id(entityId)
                .user(new User(userId))
                .build();
        return repository.find(category).orElseThrow(NotFoundException::new);
    }
}
