package com.dev.servlet.web.controller.internal;

import com.dev.servlet.application.port.in.category.DeleteCategoryUseCasePort;
import com.dev.servlet.application.port.in.category.GetCategoryDetailUseCasePort;
import com.dev.servlet.application.port.in.category.ListCategoryUseCasePort;
import com.dev.servlet.application.port.in.category.RegisterCategoryUseCasePort;
import com.dev.servlet.application.port.in.category.UpdateCategoryUseCasePort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.web.controller.CategoryControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Collection;

@ApplicationScoped
@NoArgsConstructor
public class CategoryController extends BaseController implements CategoryControllerApi {
    @Inject
    private GetCategoryDetailUseCasePort detailUseCasePort;
    @Inject
    private DeleteCategoryUseCasePort deleteUseCasePort;
    @Inject
    private UpdateCategoryUseCasePort updateUseCasePort;
    @Inject
    private ListCategoryUseCasePort listUseCasePort;
    @Inject
    private RegisterCategoryUseCasePort registerUseCasePort;

    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.<Void>next(forwardTo("formCreateCategory")).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(CategoryRequest category, String auth) {
        deleteUseCasePort.delete(category, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> details(CategoryRequest category, String auth) {
        CategoryResponse response = detailUseCasePort.get(category, auth);
        return okHttpResponse(response, forwardTo("formUpdateCategory"));
    }

    @SneakyThrows
    public IHttpResponse<Void> register(CategoryRequest category, String auth) {
        CategoryResponse response = registerUseCasePort.register(category, auth);
        return newHttpResponse(201, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(CategoryRequest category, String auth) {
        CategoryResponse response = updateUseCasePort.update(category, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, String auth) {
        Collection<CategoryResponse> response = listUseCasePort.list(category, auth);
        return okHttpResponse(response, forwardTo("listCategories"));
    }

    @SneakyThrows
    public IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, String auth) {
        CategoryResponse response = detailUseCasePort.get(request, auth);
        return okHttpResponse(response, forwardTo("formListCategory"));
    }
}
