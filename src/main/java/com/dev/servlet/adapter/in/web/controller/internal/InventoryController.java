package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.controller.InventoryControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.exception.ApplicationException;
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
import com.dev.servlet.shared.vo.KeyPair;
import com.dev.servlet.shared.vo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
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

    @SneakyThrows
    public IHttpResponse<ProductResponse> forwardRegister(Query query, String auth) {
        ProductResponse response = loadProductDetails(query, auth);
        return newHttpResponse(200, response, forwardTo("formCreateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> create(InventoryCreateRequest request, String auth) {
        InventoryResponse inventory = registerInventoryPort.register(request, auth);
        return newHttpResponse(201, redirectTo(inventory.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(InventoryRequest request, String auth) {
        deleteInventoryPort.delete(request, auth);
        return HttpResponse.<Void>next(redirectToCtx("list")).build();
    }

    @SneakyThrows
    public IServletResponse list(InventoryRequest request, String auth) {
        return getServletResponse(request, auth);
    }

    @SneakyThrows
    public IServletResponse search(Query query, String auth) {
        InventoryRequest request = inventoryMapper.queryToInventory(query);
        return getServletResponse(request, auth);
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> findById(InventoryRequest request, String auth) {
        InventoryResponse inventory = inventoryDetailPort.get(request, auth);
        return okHttpResponse(inventory, forwardTo("formListItem"));
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> details(InventoryRequest request, String auth) {
        InventoryResponse inventory = inventoryDetailPort.get(request, auth);
        return okHttpResponse(inventory, forwardTo("formUpdateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(InventoryRequest request, String auth) {
        InventoryResponse inventory = updateInventoryPort.update(request, auth);
        return newHttpResponse(204, redirectTo(inventory.getId()));
    }

    private IServletResponse getServletResponse(InventoryRequest request, String auth) throws ApplicationException {
        Collection<InventoryResponse> inventories = listInventoryPort.list(request, auth);
        Collection<CategoryResponse> categories = listCategoryPort.list(null, auth);
        Set<KeyPair> data = Set.of(
                new KeyPair("items", inventories),
                new KeyPair("categories", categories)
        );
        return newServletResponse(data, forwardTo("listItems"));
    }

    private ProductResponse loadProductDetails(Query query, String auth) throws ApplicationException {
        if (query == null || !query.has("productId")) return null;
        String productId = query.get("productId");
        ProductRequest request = ProductRequest.builder().id(productId).build();
        return productDetailPort.get(request, auth);
    }
}
