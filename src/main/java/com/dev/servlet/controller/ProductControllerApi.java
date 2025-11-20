package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.CategoryResponse;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;

import java.util.Collection;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;
import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@Controller("product")
public interface ProductControllerApi {

    @RequestMapping(value = "/create", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> register(ProductRequest request, @Authorization String auth);

    @RequestMapping("/new")
    IHttpResponse<Collection<CategoryResponse>> forward(@Authorization String auth);

    @RequestMapping(value = "/edit/{id}", jsonType = ProductRequest.class)
    IServletResponse edit(ProductRequest request, @Authorization String auth);

    @RequestMapping(value = "/search")
    IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth);

    @RequestMapping(value = "/list", jsonType = ProductRequest.class)
    IServletResponse list(IPageRequest pageRequest, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = ProductRequest.class)
    IHttpResponse<ProductResponse> getProductDetail(ProductRequest request, @Authorization String auth);

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> update(ProductRequest request, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> delete(ProductRequest filter, @Authorization String auth);

    @RequestMapping(value = "/scrape", method = GET)
    IHttpResponse<Void> scrape(@Authorization String auth, @Property("app.env") String environment, @Property("scrape_product_url") String url);
}

