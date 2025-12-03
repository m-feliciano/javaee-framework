package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;

import java.util.Collection;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("category")
public interface CategoryControllerApi {
    @RequestMapping(value = "/new")
    IHttpResponse<Void> forwardRegister();

    @RequestMapping(value = "/create", method = POST, jsonType = CategoryRequest.class)
    IHttpResponse<Void> register(CategoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/list", jsonType = CategoryRequest.class)
    IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = CategoryRequest.class)
    IHttpResponse<Void> delete(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/details/{id}", jsonType = CategoryRequest.class)
    IHttpResponse<CategoryResponse> details(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = CategoryRequest.class)
    IHttpResponse<Void> update(CategoryRequest category, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = CategoryRequest.class)
    IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, @Authorization String auth);
}
