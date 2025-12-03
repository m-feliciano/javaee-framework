package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.shared.vo.Query;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("inventory")
public interface InventoryControllerApi {
    @RequestMapping(value = "/new")
    IHttpResponse<ProductResponse> forwardRegister(Query query, @Authorization String auth);

    @RequestMapping(value = "/create", method = POST, jsonType = InventoryCreateRequest.class)
    IHttpResponse<Void> create(InventoryCreateRequest request, @Authorization String auth);

    @RequestMapping(value = "/list", jsonType = InventoryRequest.class)
    IServletResponse list(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/search")
    IServletResponse search(Query query, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = InventoryRequest.class)
    IHttpResponse<Void> delete(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = InventoryRequest.class)
    IHttpResponse<InventoryResponse> findById(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/details/{id}", jsonType = InventoryRequest.class)
    IHttpResponse<InventoryResponse> details(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = InventoryRequest.class)
    IHttpResponse<Void> update(InventoryRequest request, @Authorization String auth);
}
