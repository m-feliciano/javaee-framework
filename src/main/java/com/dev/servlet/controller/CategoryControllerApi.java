package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.CategoryRequest;
import com.dev.servlet.domain.response.CategoryResponse;

import java.util.Collection;

import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@Controller("category")
public interface CategoryControllerApi {

    @RequestMapping(value = "/new")
    IHttpResponse<Void> forwardRegister();

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = CategoryRequest.class)
    IHttpResponse<Void> delete(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/edit/{id}", jsonType = CategoryRequest.class)
    IHttpResponse<CategoryResponse> edit(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/create", method = POST, jsonType = CategoryRequest.class)
    IHttpResponse<Void> register(CategoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = CategoryRequest.class)
    IHttpResponse<Void> update(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/list", jsonType = CategoryRequest.class)
    IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = CategoryRequest.class)
    IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, @Authorization String auth);
}