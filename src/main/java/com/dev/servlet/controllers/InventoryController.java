package com.dev.servlet.controllers;

import com.dev.servlet.dto.CategoryDTO;
import com.dev.servlet.dto.InventoryDTO;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.IServletResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.model.InventoryModel;
import com.dev.servlet.pojo.Inventory;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.KeyPair;
import com.dev.servlet.pojo.records.Request;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;


@NoArgsConstructor
@Controller(path = "/inventory")
public final class InventoryController extends BaseController<Inventory, Long> {

    @Inject
    CategoryController categoryController;

    @Inject
    public InventoryController(InventoryModel model) {
        super(model);
    }

    private InventoryModel getModel() {
        return (InventoryModel) super.getBaseModel();
    }

    /**
     * Forward to the register form.
     *
     * @return the response {@linkplain IHttpResponse} with the next path
     */
    @RequestMapping(value = NEW, method = "GET")
    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.ofNext(super.forwardTo("formCreateItem"));
    }

    /**
     * Create a new item.
     *
     * @param request {@linkplain Request}
     * @return the response {@linkplain IHttpResponse} with the next path
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = CREATE, method = "POST")
    public IHttpResponse<Void> create(Request request) throws ServiceException {
        InventoryDTO inventory = this.getModel().create(request);
        // Created
        return super.newHttpResponse(201, null, super.redirectTo(inventory.getId()));
    }

    /**
     * Delete an item.
     *
     * @param request {@linkplain Request}
     * @return the response {@linkplain IHttpResponse} with the next path
     */
    @RequestMapping(value = DELETE, method = "POST")
    public IHttpResponse<Void> delete(Request request) {
        this.getModel().delete(request);

        return HttpResponse.ofNext(super.redirectTo(LIST));
    }

    /**
     * List all items.
     *
     * @param request {@linkplain Request}
     * @return the response {@linkplain IServletResponse} with the next path
     */
    @RequestMapping(value = LIST, method = "GET")
    public IServletResponse list(Request request) {
        Collection<InventoryDTO> inventories = this.getModel().list(request);
        Collection<CategoryDTO> categories = categoryController.list(request).body();

        Set<KeyPair> data = Set.of(
                new KeyPair("items", inventories),
                new KeyPair("categories", categories)
        );

        return super.newServletResponse(data, super.forwardTo("listItems"));
    }

    /**
     * List an item by ID.
     *
     * @param request {@linkplain Request}
     * @return the response {@linkplain IServletResponse} with the next path
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = "/{id}", method = "GET")
    public IHttpResponse<InventoryDTO> listById(Request request) throws ServiceException {
        InventoryDTO inventory = this.getModel().listById(request);
        // OK
        return super.newHttpResponse(200, inventory, super.forwardTo("formListItem"));
    }

    /**
     * Edit an item.
     *
     * @param request {@linkplain Request}
     * @return the response {@linkplain IServletResponse} with the next path
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = EDIT, method = "GET")
    public IHttpResponse<InventoryDTO> edit(Request request) throws ServiceException {
        InventoryDTO inventory = this.getModel().listById(request);
        // OK
        return super.newHttpResponse(200, inventory, super.forwardTo("formUpdateItem"));
    }

    /**
     * Update an item.
     *
     * @param request {@linkplain Request}
     * @return the response {@linkplain IServletResponse} with the next path
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = UPDATE, method = "POST")
    public IHttpResponse<Void> update(Request request) throws ServiceException {
        InventoryDTO inventory = this.getModel().update(request);
        // No content
        return super.newHttpResponse(204, null, super.redirectTo(inventory.getId()));
    }
}
