package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.shared.vo.Query;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("inventory")
public interface InventoryControllerApi {
    @RequestMapping(
            value = "/new",
            description = "Forward to the inventory registration page."
    )
    IHttpResponse<ProductResponse> forwardRegister(Query query, String auth);

    @RequestMapping(
            value = "/create",
            method = POST,
            jsonType = InventoryCreateRequest.class,
            description = "Create a new inventory record."
    )
    IHttpResponse<Void> create(InventoryCreateRequest request, String auth);

    @RequestMapping(
            value = "/list",
            jsonType = InventoryRequest.class,
            description = "Retrieve the list of inventory records."
    )
    IServletResponse list(IPageRequest pageRequest, InventoryRequest request, String auth);

    @RequestMapping(value = "/search", description = "Search inventory records based on query parameters.")
    IServletResponse search(Query query, IPageRequest pageRequest, String auth);

    @RequestMapping(
            value = "/delete/{id}",
            method = POST,
            jsonType = InventoryRequest.class,
            description = "Delete an inventory record by ID."
    )
    IHttpResponse<Void> delete(InventoryRequest request, String auth);

    @RequestMapping(
            value = "/list/{id}",
            jsonType = InventoryRequest.class,
            description = "Retrieve detailed information about a specific inventory record."
    )
    IHttpResponse<InventoryResponse> findById(InventoryRequest request, String auth);

    @RequestMapping(
            value = "/details/{id}",
            jsonType = InventoryRequest.class,
            description = "Retrieve details of a specific inventory record by ID."
    )
    IHttpResponse<InventoryResponse> details(InventoryRequest request, String auth);

    @RequestMapping(
            value = "/update/{id}",
            method = POST,
            jsonType = InventoryRequest.class,
            description = "Update an existing inventory record."
    )
    IHttpResponse<Void> update(InventoryRequest request, String auth);
}
