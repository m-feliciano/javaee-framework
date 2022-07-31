package servlets.inventory;

import controllers.ProductController;
import domain.Inventory;
import domain.Product;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

import static servlets.base.Base.*;

public class InventoryServlet extends BaseInventory {
    protected final EntityManager emp = JPAUtil.getEntityManager();
    private final ProductController productController = new ProductController(emp);

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter("action") == null) {
            logger.error("Error: action can't be null");
            req.setAttribute(ERROR, "Action can't be null");
        }
        switch (req.getParameter("action")) {
            case "CreateItem" -> {
                return createItem(req, resp);
            }
            case "ListItems" -> {
                return listItem(req, resp);
            }
            case "UpdateItem" -> {
                return updateItem(req, resp);
            }
            case "NewItem" -> {
                return newItem();
            }
            case "EditItem" -> {
                return editItem(req, resp);
            }
            case "DeleteItem" -> {
                return deleteItem(req, resp);
            }
            default -> {
                logger.error("Error: action not found");
                req.setAttribute(ERROR, "Action not found");
            }

        }
        return FORWARD_PAGES_NOT_FOUND_JSP;
    }

    /**
     * Gets the item.
     *
     * @param req  the request
     * @param resp the response
     * @return string
     */

    private String createItem(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating a inventory item");

        Product product = productController.findById(Long.parseLong(req.getParameter(PRODUCT_ID)));
        int quantity = Integer.parseInt(req.getParameter(QUANTITY));
        Inventory item = new Inventory(product, quantity, req.getParameter(DESCRIPTION));
        controller.save(item);
        req.setAttribute(ITEM, item);
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID + item.getId();
    }

    /**
     * list item or items.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    private String listItem(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing items by filter");

        String id = req.getParameter(ID);
        if (!Objects.isNull(id)) {
            Inventory inventory = controller.findById(Long.parseLong(id));
            if (Objects.isNull(inventory)) {
                return FORWARD_PAGES_NOT_FOUND_JSP;
            }

            req.setAttribute(ITEM, inventory);
            return FORWARD_PAGES_INVENTORY_FORM_LIST_ITEM_JSP;
        }

        String param = req.getParameter("param");
        String value = req.getParameter("value");
        List<Inventory> inventories;
        if (!Objects.isNull(param) && !Objects.isNull(value)) {
            if (param.equals("name")) {
                inventories = controller.findAllByProductName(value);
            } else {
                inventories = controller.findAllByDescription(value);
            }
            req.setAttribute(ITEMS, inventories);
        } else {
            inventories = controller.findAll();
            req.setAttribute(ITEMS, inventories);
        }

        return FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP;
    }

    /**
     * redirect to form create item.
     *
     * @return the string
     */

    private String newItem() {
        logger.info("doGET redirecting to form createItem");
        return FORWARD_PAGES_INVENTORY_FORM_CREATE_ITEM_JSP;
    }

    /**
     * update item.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    private String updateItem(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating inventory item");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = productController.findById(Long.parseLong(req.getParameter(PRODUCT_ID)));
        Inventory item = controller.findById(Long.parseLong(req.getParameter(ID)));
        int quantity = Integer.parseInt(req.getParameter(QUANTITY));
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setDescription(req.getParameter(DESCRIPTION));
        controller.update(item);
        req.setAttribute(ITEM, item);
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID + item.getId();
    }

    /**
     * update item.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    private String editItem(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET editing item");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        req.setAttribute(ITEM, controller.findById(Long.parseLong(req.getParameter(ID))));
        return FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP;
    }

    /**
     * delete item.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    private String deleteItem(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET deleting item");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        controller.delete(Long.parseLong(req.getParameter(ID)));
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS;
    }
}
