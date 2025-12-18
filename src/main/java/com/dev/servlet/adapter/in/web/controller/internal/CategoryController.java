package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Cache;
import com.dev.servlet.adapter.in.web.controller.CategoryControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.category.DeleteCategoryPort;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.category.RegisterCategoryPort;
import com.dev.servlet.application.port.in.category.UpdateCategoryPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CategoryController extends BaseController implements CategoryControllerApi {
    @Inject
    private GetCategoryDetailPort detailPort;
    @Inject
    private DeleteCategoryPort deletePort;
    @Inject
    private UpdateCategoryPort updatePort;
    @Inject
    private ListCategoryPort listPort;
    @Inject
    private RegisterCategoryPort registerPort;

    @Override
    protected Class<CategoryController> implementation() {
        return CategoryController.class;
    }

    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.<Void>next(forwardTo("formCreateCategory")).build();
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> details(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = detailPort.get(category, auth);
        return okHttpResponse(response, forwardTo("formUpdateCategory"));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(CategoryRequest category, @Authorization String auth) {
        deletePort.delete(category, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> register(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = registerPort.register(category, auth);
        return newHttpResponse(201, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(CategoryRequest category, @Authorization String auth) {
        CategoryResponse response = updatePort.update(category, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, @Authorization String auth) {
        Collection<CategoryResponse> response = listPort.list(category, auth);
        return okHttpResponse(response, forwardTo("listCategories"));
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, @Authorization String auth) {
        CategoryResponse response = detailPort.get(request, auth);
        return okHttpResponse(response, forwardTo("formListCategory"));
    }
}
