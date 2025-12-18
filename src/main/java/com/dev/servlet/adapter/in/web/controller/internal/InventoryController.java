package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.controller.InventoryControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.product.ProductDetailPort;
import com.dev.servlet.application.port.in.stock.DeleteInventoryPort;
import com.dev.servlet.application.port.in.stock.GetInventoryDetailPort;
import com.dev.servlet.application.port.in.stock.ListInventoryPort;
import com.dev.servlet.application.port.in.stock.RegisterInventoryPort;
import com.dev.servlet.application.port.in.stock.UpdateInventoryPort;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.shared.vo.KeyPair;
import com.dev.servlet.shared.vo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class InventoryController extends BaseController implements InventoryControllerApi {
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private ListCategoryPort listCategoryPort;
    @Inject
    private ListInventoryPort listInventoryPort;
    @Inject
    private UpdateInventoryPort updateInventoryPort;
    @Inject
    private RegisterInventoryPort registerInventoryPort;
    @Inject
    private DeleteInventoryPort deleteInventoryPort;
    @Inject
    private GetInventoryDetailPort inventoryDetailPort;
    @Inject
    private ProductDetailPort productDetailPort;

    @Override
    protected Class<InventoryController> implementation() {
        return InventoryController.class;
    }

    @SneakyThrows
    public IHttpResponse<ProductResponse> forwardRegister(Query query, @Authorization String auth) {
        ProductResponse response = loadProductDetails(query, auth);
        return newHttpResponse(200, response, forwardTo("formCreateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> create(InventoryCreateRequest request, @Authorization String auth) {
        InventoryResponse inventory = registerInventoryPort.register(request, auth);
        return newHttpResponse(201, redirectTo(inventory.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(InventoryRequest request, @Authorization String auth) {
        deleteInventoryPort.delete(request, auth);
        return HttpResponse.<Void>next(redirectToCtx("list")).build();
    }

    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, InventoryRequest request, @Authorization String auth) {
        Inventory inventory = inventoryMapper.toInventory(request);
        pageRequest.setFilter(inventory);
        IServletResponse response = getServletResponse(pageRequest, auth);
        log.debug("List completed for inventory: {}", request);
        return response;
    }

    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth) {
        Inventory inventory = inventoryMapper.toInventory(inventoryMapper.queryToInventory(query));
        pageRequest.setFilter(inventory);
        IServletResponse response = getServletResponse(pageRequest, auth);
        log.debug("Search completed with query: {}", query);
        return response;
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> findById(InventoryRequest request, @Authorization String auth) {
        InventoryResponse inventory = inventoryDetailPort.get(request, auth);
        return okHttpResponse(inventory, forwardTo("formListItem"));
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> details(InventoryRequest request, @Authorization String auth) {
        InventoryResponse inventory = inventoryDetailPort.get(request, auth);
        return okHttpResponse(inventory, forwardTo("formUpdateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(InventoryRequest request, @Authorization String auth) {
        InventoryResponse inventory = updateInventoryPort.update(request, auth);
        return newHttpResponse(204, redirectTo(inventory.getId()));
    }

    private IServletResponse getServletResponse(IPageRequest pageRequest, String auth) throws AppException {
        IPageable<InventoryResponse> page = listInventoryPort.getAllPageable(
                pageRequest, auth, inventoryMapper::toResponse);
        Collection<CategoryResponse> categories = listCategoryPort.list(null, auth);

        Set<KeyPair> container = Set.of(
                new KeyPair("pageable", page),
                new KeyPair("categories", categories)
        );
        return newServletResponse(container, forwardTo("listItems"));
    }

    private ProductResponse loadProductDetails(Query query, String auth) throws AppException {
        if (query == null || !query.has("productId")) return null;
        String productId = query.get("productId");
        ProductRequest request = ProductRequest.builder().id(productId).build();
        return productDetailPort.get(request, auth);
    }
}
