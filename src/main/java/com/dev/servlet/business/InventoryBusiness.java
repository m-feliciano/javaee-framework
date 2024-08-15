package com.dev.servlet.business;

import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.Product;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.dto.InventoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.InventoryMapper;
import com.dev.servlet.business.base.BaseRequest;

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
public class InventoryBusiness extends BaseRequest {

    private static final String FORWARD_PAGE_LIST = "forward:pages/inventory/formListItem.jsp";
    private static final String FORWARD_PAGE_LIST_ITEMS = "forward:pages/inventory/listItems.jsp";
    private static final String FORWARD_PAGE_CREATE = "forward:pages/inventory/formCreateItem.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/inventory/formUpdateItem.jsp";

    private static final String REDIRECT_ACTION_LIST_ALL = "redirect:inventory?action=list";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:inventory?action=list&id=";

    @Inject
    private InventoryController controller;
    @Inject
    private CategoryBusiness categoryBusiness;
    @Inject
    private ProductShared productShared;

    public InventoryBusiness() {
    }

    public InventoryBusiness(InventoryController controller,
                             CategoryBusiness categoryBusiness,
                             ProductShared productShared) {
        this.controller = controller;
        this.categoryBusiness = categoryBusiness;
        this.productShared = productShared;
    }

    /**
     * Forward page form
     *
     * @param
     * @return the next path
     */
    @ResourcePath(value = NEW)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGE_CREATE;
    }

    /**
     * Create the item.
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(value = CREATE)
    public String register(StandardRequest request) {
        int quantity = Integer.parseInt(getParameter(request, "quantity"));
        String description = getParameter(request, "description");
        Long productId = Long.valueOf(getParameter(request, "productId"));

        Product product = new Product(productId);
        Inventory item = new Inventory(product, quantity, description);
        item.setStatus(StatusEnum.ACTIVE.getName());
        item.setUser(getUser(request));
        controller.save(item);

        request.servletRequest().setAttribute("item", item);
        return REDIRECT_ACTION_LIST_BY_ID + item.getId();
    }

    /**
     * list item or items.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = LIST)
    public String list(StandardRequest request) {
        String id = getParameter(request, "id");
        if (id != null) {
            Inventory inventory = controller.findById(Long.valueOf(id));
            if (inventory != null) {
                request.servletRequest().setAttribute("item", InventoryMapper.from(inventory));
                return FORWARD_PAGE_LIST;
            }
            return FORWARD_PAGES_NOT_FOUND;
        }

        List<InventoryDto> list = findAll(request);
        request.servletRequest().setAttribute("items", list);
        request.servletRequest().setAttribute("categories", categoryBusiness.findAll(request));
        return FORWARD_PAGE_LIST_ITEMS;
    }

    /**
     * update one.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = UPDATE)
    public String update(StandardRequest standardRequest) {
        Inventory inventory = controller.findById(Long.valueOf(getParameter(standardRequest, "id")));
        inventory.setQuantity(Integer.parseInt(getParameter(standardRequest, "quantity")));
        inventory.setDescription(getParameter(standardRequest, "description"));

        Long productId = Long.valueOf(getParameter(standardRequest, "productId"));

        ProductDto productDto = productShared.find(productId);
        if (productDto == null) {
            standardRequest.servletResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            standardRequest.servletRequest().setAttribute("error", "ERROR: Product ID " + getParameter(standardRequest, "productId") + " was not found.");
            standardRequest.servletRequest().setAttribute("item", inventory);
            return this.forwardRegister(standardRequest);
        }

        inventory.setProduct(new Product(productId));
        inventory = controller.update(inventory);
        standardRequest.servletRequest().setAttribute("item", InventoryMapper.from(inventory));
        return REDIRECT_ACTION_LIST_BY_ID + inventory.getId();
    }

    /**
     * edit one.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = EDIT)
    public String edit(StandardRequest standardRequest) {
        Long id = Long.valueOf(getParameter(standardRequest, "id"));
        Inventory inventory = new Inventory(id);
        inventory = controller.findById(id);
        standardRequest.servletRequest().setAttribute("item", InventoryMapper.from(inventory));
        return FORWARD_PAGE_UPDATE;
    }

    /**
     * delete one.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = DELETE)
    public String delete(StandardRequest request) {
        Long id = Long.valueOf(getParameter(request, "id"));
        Inventory obj = new Inventory(id);
        controller.delete(obj);
        return REDIRECT_ACTION_LIST_ALL;
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

    public boolean hasInventory(Inventory product) {
        return controller.hasInventory(product);
    }
}
