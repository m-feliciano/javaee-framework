package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.dto.InventoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.interfaces.ResourceMapping;
import com.dev.servlet.mapper.InventoryMapper;
import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.Inventory;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.StandardRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
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
@ResourcePath("inventory")
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
    @ResourceMapping(NEW)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGES_INVENTORY + "formCreateItem.jsp";
    }

    /**
     * Create the item.
     *
     * @param request
     * @return the next path
     */
    @ResourceMapping(CREATE)
    public String create(StandardRequest request) throws ServiceException {
        int quantity = Integer.parseInt(request.getRequiredParameter("quantity"));
        Long productId = Long.valueOf(request.getRequiredParameter("productId"));

        Product product = new Product(productId);
        Inventory item = new Inventory(product, quantity, request.getParameter("description"));
        item.setStatus(StatusEnum.ACTIVE.value);
        item.setUser(getUser(request));
        controller.save(item);

        request.setAttribute("item", item);
        request.setStatus(HttpServletResponse.SC_CREATED);
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", item.getId().toString());
    }

    /**
     * list item or items.
     *
     * @param request
     * @return the string
     */
    @ResourceMapping(LIST)
    public String list(StandardRequest request) throws ServiceException {
        Long resourceId = request.getId();
        if (resourceId != null) {
            Inventory inventory = new Inventory(resourceId);
            inventory.setUser(getUser(request));
            inventory = controller.find(inventory);

            if (inventory == null) throwResourceNotFoundException(resourceId);

            request.setStatus(HttpServletResponse.SC_OK);
            request.setAttribute("item", InventoryMapper.from(inventory));
            return FORWARD_PAGES_INVENTORY + "formListItem.jsp";
        }

        List<InventoryDto> list = findAll(request);
        request.setAttribute("items", list);
        request.setAttribute("categories", categoryBusiness.getAllFromCache(request));
        return FORWARD_PAGES_INVENTORY + "listItems.jsp";
    }

    /**
     * update one.
     *
     * @param request
     * @return the string
     */
    @ResourceMapping(UPDATE)
    public String update(StandardRequest request) throws ServiceException, IOException {
        if (request.getId() == null) throwResourceNotFoundException(null);

        Inventory inventory = new Inventory(request.getId());
        inventory.setUser(getUser(request));

        inventory = controller.find(inventory);
        if (inventory == null) throwResourceNotFoundException(request.getId());

        inventory.setQuantity(Integer.parseInt(request.getRequiredParameter("quantity")));
        inventory.setDescription(request.getParameter("description"));

        String productId = request.getRequiredParameter("productId");

        Long idProduct = Long.valueOf(productId);
        ProductDto productDto = productShared.find(idProduct);
        if (productDto == null) {
            request.setAttribute("error", "ERROR: Product ID " + productId + " was not found.");
            request.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return this.forwardRegister(request);
        }

        inventory.setProduct(new Product(idProduct));
        inventory = controller.update(inventory);
        request.setAttribute("item", InventoryMapper.from(inventory));
        request.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", inventory.getId().toString());
    }

    /**
     * edit one.
     *
     * @param request
     * @return the string
     */
    @ResourceMapping(EDIT)
    public String edit(StandardRequest request) throws ServiceException {
        if (request.getId() == null) throwResourceNotFoundException(null);

        Inventory inventory = new Inventory(request.getId());
        inventory.setUser(getUser(request));
        inventory = controller.find(inventory);

        if (inventory == null) throwResourceNotFoundException(request.getId());

        request.setAttribute("item", InventoryMapper.from(inventory));
        return FORWARD_PAGES_INVENTORY + "formUpdateItem.jsp";
    }

    /**
     * delete one.
     *
     * @param request
     * @return the string
     */
    @ResourceMapping(DELETE)
    public String delete(StandardRequest request) {
        Inventory obj = new Inventory(request.getId());
        obj.setUser(getUser(request));

        controller.delete(obj);
        request.setStatus(HttpServletResponse.SC_NO_CONTENT);
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

        String param = request.getQuery().type();
        String value = request.getQuery().search();
        if (param != null && value != null) {
            if (param.equals("name")) {
                Product product = new Product();
                product.setName(value.trim());
                String category = request.getParameter("category");
                if (category != null && !category.isEmpty()) {
                    product.setCategory(new Category(Long.valueOf(category)));
                }

                inventory.setProduct(product);
            } else {
                inventory.setDescription(value.trim());
            }
        }

        Collection<Inventory> inventories = controller.findAll(inventory);
        return inventories.stream().map(InventoryMapper::from).toList();
    }
}
