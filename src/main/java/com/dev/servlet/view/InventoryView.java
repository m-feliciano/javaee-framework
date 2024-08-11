package com.dev.servlet.view;

import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.Product;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.dto.InventoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.Inject;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.InventoryMapper;
import com.dev.servlet.view.base.BaseRequest;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class InventoryView extends BaseRequest {

    private static final String FORWARD_PAGE_LIST = "forward:pages/inventory/formListItem.jsp";
    private static final String FORWARD_PAGE_LIST_ITEMS = "forward:pages/inventory/listItems.jsp";
    private static final String FORWARD_PAGE_CREATE = "forward:pages/inventory/formCreateItem.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/inventory/formUpdateItem.jsp";

    private static final String REDIRECT_ACTION_LIST_ALL = "redirect:inventoryView?action=list";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:inventoryView?action=list&id=";

    private InventoryController controller;
    @Inject
    private CategoryView categoryView;
    @Inject
    private ProductView productView;

    public InventoryView() {
    }

    public InventoryView(EntityManager entityManager) {
        this.controller = new InventoryController(entityManager);
    }

    /**
     * Forward page form
     *
     * @param
     * @return the next path
     */
    @ResourcePath(value = NEW, forward = true)
    public String forwardRegister() {
        return FORWARD_PAGE_CREATE;
    }

    /**
     * Create the item.
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = CREATE)
    public String register(StandardRequest standardRequest) {
        HttpServletRequest req = standardRequest.getRequest();

        int quantity = Integer.parseInt(getParameter(req, "quantity"));
        String description = getParameter(req, "description");
        Long productId = Long.valueOf(getParameter(req, "productId"));

        Product product = new Product(productId);
        Inventory item = new Inventory(product, quantity, description);
        item.setStatus(StatusEnum.ACTIVE.getName());
        item.setUser(getUser(req));
        controller.save(item);

        req.setAttribute("item", item);
        return REDIRECT_ACTION_LIST_BY_ID + item.getId();
    }

    /**
     * list item or items.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = LIST)
    public String list(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();
        String id = getParameter(request, "id");
        if (id != null) {
            Inventory inventory = controller.findById(Long.valueOf(id));
            if (inventory != null) {
                request.setAttribute("item", InventoryMapper.from(inventory));
                return FORWARD_PAGE_LIST;
            }
            return FORWARD_PAGES_NOT_FOUND;
        }

        List<InventoryDto> list = findAll(request);
        request.setAttribute("items", list);
        request.setAttribute("categories", categoryView.findAll(request));
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
        HttpServletRequest request = standardRequest.getRequest();

        Inventory inventory = controller.findById(Long.valueOf(getParameter(request, "id")));
        inventory.setQuantity(Integer.parseInt(getParameter(request, "quantity")));
        inventory.setDescription(getParameter(request, "description"));

        Long productId = Long.valueOf(getParameter(request, "productId"));

        Product product = new Product(productId);
        ProductDto productDto = productView.find(product);
        if (productDto == null) {
            standardRequest.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", "ERROR: Product ID " + getParameter(request, "productId") + " was not found.");
            request.setAttribute("item", inventory);
            return this.forwardRegister();
        }

        inventory.setProduct(new Product(productId));
        controller.update(inventory);
        request.setAttribute("item", InventoryMapper.from(inventory));
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
        HttpServletRequest request = standardRequest.getRequest();
        Long id = Long.valueOf(getParameter(request, "id"));
        Inventory inventory = new Inventory(id);
        inventory = controller.findById(id);
        request.setAttribute("item", InventoryMapper.from(inventory));
        return FORWARD_PAGE_UPDATE;
    }

    /**
     * delete one.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = DELETE)
    public String delete(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();
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
    private List<InventoryDto> findAll(HttpServletRequest request) {
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
