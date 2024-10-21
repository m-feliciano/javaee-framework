package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.dto.InventoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.InventoryMapper;
import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.Inventory;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.StandardRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Inventory business.
 * <p>
 * This class is responsible for handling the inventory business logic.
 *
 * @see BaseRequest
 * @since 1.0
 */
@Singleton
@IService("inventory")
public class InventoryBusiness extends BaseRequest {
    public static final String FORWARD_PAGES_INVENTORY = "forward:pages/inventory/";
    public static final String REDIRECT_VIEW_INVENTORY = "redirect:/view/inventory/";
    private static final String REDIRECT_ACTION_LIST_BY_ID = REDIRECT_VIEW_INVENTORY + "list/<id>";

    private InventoryController controller;
    private CategoryBusiness categoryBusiness;
    private ProductShared productShared;

    public InventoryBusiness() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(InventoryController controller,
                                CategoryBusiness categoryBusiness,
                                ProductShared productShared) {
        this.controller = controller;
        this.categoryBusiness = categoryBusiness;
        this.productShared = productShared;
    }

    /**
     * Forward currentPage form
     *
     * @param
     * @return the next path
     */
    @ResourcePath(NEW)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGES_INVENTORY + "formCreateItem.jsp";
    }

    /**
     * Create the item.
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(CREATE)
    public String register(StandardRequest request) {
        int quantity = Integer.parseInt(getParameter(request, "quantity"));
        String description = getParameter(request, "description");
        Long productId = Long.valueOf(getParameter(request, "productId"));

        Product product = new Product(productId);
        Inventory item = new Inventory(product, quantity, description);
        item.setStatus(StatusEnum.ACTIVE.value);
        item.setUser(getUser(request));
        controller.save(item);

        request.servletRequest().setAttribute("item", item);
        request.servletResponse().setStatus(HttpServletResponse.SC_CREATED);
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", item.getId().toString());
    }

    /**
     * list item or items.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(LIST)
    public String list(StandardRequest request) throws Exception {
        Long resourceId = request.requestObject().resourceId();
        if (resourceId != null) {
            Inventory inventory = new Inventory(resourceId);
            inventory.setUser(getUser(request));
            inventory = controller.find(inventory);
            if (inventory == null) {
                request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }

            request.servletResponse().setStatus(HttpServletResponse.SC_OK);
            request.servletRequest().setAttribute("item", InventoryMapper.from(inventory));
            return FORWARD_PAGES_INVENTORY + "formListItem.jsp";
        }

        List<InventoryDto> list = findAll(request);
        request.servletRequest().setAttribute("items", list);
        request.servletRequest().setAttribute("categories", categoryBusiness.findAll(request));
        return FORWARD_PAGES_INVENTORY + "listItems.jsp";
    }

    /**
     * update one.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(UPDATE)
    public String update(StandardRequest standardRequest) throws Exception {
        Inventory inventory = new Inventory(standardRequest.requestObject().resourceId());
        inventory.setUser(getUser(standardRequest));

        inventory = controller.find(inventory);
        if (inventory == null) {
            standardRequest.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        inventory.setQuantity(Integer.parseInt(getParameter(standardRequest, "quantity")));
        inventory.setDescription(getParameter(standardRequest, "description"));

        Long productId = Long.valueOf(getParameter(standardRequest, "productId"));

        ProductDto productDto = productShared.find(productId);
        if (productDto == null) {
            standardRequest.servletRequest().setAttribute("error", "ERROR: Product ID " + productId + " was not found.");
            standardRequest.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return this.forwardRegister(standardRequest);
        }

        inventory.setProduct(new Product(productId));
        inventory = controller.update(inventory);
        standardRequest.servletRequest().setAttribute("item", InventoryMapper.from(inventory));
        standardRequest.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", inventory.getId().toString());
    }

    /**
     * edit one.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(EDIT)
    public String edit(StandardRequest request) throws Exception {
        Inventory inventory = new Inventory(request.requestObject().resourceId());
        inventory.setUser(getUser(request));
        inventory = controller.find(inventory);
        if (inventory == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        request.servletRequest().setAttribute("item", InventoryMapper.from(inventory));
        return FORWARD_PAGES_INVENTORY + "formUpdateItem.jsp";
    }

    /**
     * delete one.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(DELETE)
    public String delete(StandardRequest request) {
        Inventory obj = new Inventory(request.requestObject().resourceId());
        obj.setUser(getUser(request));

        controller.delete(obj);
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_VIEW_INVENTORY + "list";
    }

    /**
     * Find All
     *
     * @param request
     * @return the next path
     */
    private List<InventoryDto> findAll(StandardRequest request) {
        Inventory inventory = new Inventory();
        inventory.setUser(getUser(request));

        String param = getParameter(request, PARAM);
        String value = getParameter(request, VALUE);
        if (param != null && value != null) {
            if (param.equals("name")) {
                Product product = new Product();
                product.setName(value);
                String category = getParameter(request, "category");
                if (category != null && !category.isEmpty()) {
                    product.setCategory(new Category(Long.valueOf(category)));
                }

                inventory.setProduct(product);
            } else {
                inventory.setDescription(value);
            }
        }

        List<Inventory> inventories = controller.findAll(inventory);
        return inventories.stream().map(InventoryMapper::from).toList();
    }
}
