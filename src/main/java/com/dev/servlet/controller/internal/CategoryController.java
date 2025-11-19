package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.CategoryControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.CategoryRequest;
import com.dev.servlet.domain.response.CategoryResponse;
import com.dev.servlet.service.ICategoryService;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

@NoArgsConstructor
@Singleton
public class CategoryController extends BaseController implements CategoryControllerApi {

    @Inject
    private ICategoryService categoryService;

    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.<Void>next(forwardTo("formCreateCategory")).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(CategoryRequest category, @Authorization String auth) {
        categoryService.delete(category, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> edit(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = categoryService.getCategoryDetail(category, auth);
        return okHttpResponse(response, forwardTo("formUpdateCategory"));
    }

    @SneakyThrows
    public IHttpResponse<Void> register(CategoryRequest request, @Authorization String auth) {
        CategoryResponse response = categoryService.register(request, auth);
        return newHttpResponse(201, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = categoryService.update(category, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, @Authorization String auth) {
        Collection<CategoryResponse> response = categoryService.list(category, auth);
        return okHttpResponse(response, forwardTo("listCategories"));
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, @Authorization String auth) {
        CategoryResponse response = categoryService.getCategoryDetail(request, auth);
        return okHttpResponse(response, forwardTo("formListCategory"));
    }
}
