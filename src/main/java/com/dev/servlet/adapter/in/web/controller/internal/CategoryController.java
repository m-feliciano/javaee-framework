package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.controller.CategoryControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.category.DeleteCategoryUseCase;
import com.dev.servlet.application.port.in.category.GetCategoryDetailUseCase;
import com.dev.servlet.application.port.in.category.ListCategoryUseCase;
import com.dev.servlet.application.port.in.category.RegisterCategoryUseCase;
import com.dev.servlet.application.port.in.category.UpdateCategoryUseCase;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.util.Collection;

@ApplicationScoped
public class CategoryController extends BaseController implements CategoryControllerApi {
    @Inject
    private GetCategoryDetailUseCase categoryDetailUseCase;
    @Inject
    private DeleteCategoryUseCase deleteCategoryUseCase;
    @Inject
    private UpdateCategoryUseCase updateCategoryUseCase;
    @Inject
    private ListCategoryUseCase listCategoryUseCase;
    @Inject
    private RegisterCategoryUseCase categoryUseCase;

    @Override
    protected Class<CategoryController> implementation() {
        return CategoryController.class;
    }

    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.<Void>next(forwardTo("formCreateCategory")).build();
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> details(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = categoryDetailUseCase.get(category, auth);
        return okHttpResponse(response, forwardTo("formUpdateCategory"));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(CategoryRequest category, @Authorization String auth) {
        deleteCategoryUseCase.delete(category, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> register(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = categoryUseCase.register(category, auth);
        return newHttpResponse(201, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = updateCategoryUseCase.update(category, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, @Authorization String auth) {
        Collection<CategoryResponse> response = listCategoryUseCase.list(category, auth);
        return okHttpResponse(response, forwardTo("listCategories"));
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, @Authorization String auth) {
        CategoryResponse response = categoryDetailUseCase.get(request, auth);
        return okHttpResponse(response, forwardTo("formListCategory"));
    }
}
