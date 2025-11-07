package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.service.ICategoryService;
import com.dev.servlet.domain.transfer.request.CategoryRequest;
import com.dev.servlet.domain.transfer.response.CategoryResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@NoArgsConstructor
@Singleton
@Controller("category")
public class CategoryController extends BaseController {

    @Inject
    private ICategoryService categoryService;

    @RequestMapping(value = "/new")
    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.<Void>next(forwardTo("formCreateCategory")).build();
    }

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = CategoryRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> delete(CategoryRequest category, @Authentication String auth) {
        categoryService.delete(category, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @RequestMapping(value = "/edit/{id}", jsonType = CategoryRequest.class)
    @SneakyThrows
    public IHttpResponse<CategoryResponse> edit(CategoryRequest category, @Authentication String auth) {
        CategoryResponse response = categoryService.getCategoryDetail(category, auth);
        return okHttpResponse(response, forwardTo("formUpdateCategory"));
    }

    @RequestMapping(value = "/create", method = POST, jsonType = CategoryRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> register(CategoryRequest request, @Authentication String auth) {
        CategoryResponse response = categoryService.register(request, auth);
        return newHttpResponse(201, redirectTo(response.getId()));
    }

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = CategoryRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> update(CategoryRequest category, @Authentication String auth) {
        CategoryResponse response = categoryService.update(category, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @RequestMapping(value = "/list", jsonType = CategoryRequest.class)
    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, @Authentication String auth) {
        Collection<CategoryResponse> response = categoryService.list(category, auth);
        return okHttpResponse(response, forwardTo("listCategories"));
    }

    @RequestMapping(value = "/list/{id}", jsonType = CategoryRequest.class)
    @SneakyThrows
    public IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, @Authentication String auth) {
        CategoryResponse response = categoryService.getCategoryDetail(request, auth);
        return okHttpResponse(response, forwardTo("formListCategory"));
    }
}
