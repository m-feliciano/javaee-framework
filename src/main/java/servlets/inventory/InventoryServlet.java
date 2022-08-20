package servlets.inventory;

import controllers.InventoryController;
import controllers.ProductController;
import domain.Inventory;
import domain.Product;
import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static servlets.base.Base.*;

public class InventoryServlet extends BaseInventory {
    private final ProductController productController = new ProductController(getEm());
    private final InventoryController controller = new InventoryController(getEm());

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter("action") == null) {
            logger.error("Error: action can't be null");
            req.setAttribute(ERROR, "Action can't be null");
        }
        switch (req.getParameter("action")) {
            case "create" -> {
                return create(req, resp);
            }
            case "list" -> {
                return list(req, resp);
            }
            case "update" -> {
                return update(req, resp);
            }
            case "new" -> {
                return add();
            }
            case "edit" -> {
                return edit(req, resp);
            }
            case "delete" -> {
                return delete(req, resp);
            }
            default -> {
                logger.error("Error: action not found");
                req.setAttribute(ERROR, "Action not found");
            }
        }
        return FORWARD_PAGES_NOT_FOUND_JSP;
    }

    /**
     * Create the item.
     *
     * @param req  the request
     * @param resp the response
     * @return string
     */
    private String create(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("doPOST creating a inventory item");
        Product product = Product.getProductFromRequest(req);
        product = productController.find(product);
        int quantity = Integer.parseInt(req.getParameter(QUANTITY));
        Inventory item = new Inventory(product, quantity, req.getParameter(DESCRIPTION));
        controller.save(item);
        req.setAttribute(ITEM, item);
        stopWatch.stop();
        logger.info("doPOST creating a inventory item took {} ms", stopWatch.getTime());
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID + item.getId();
    }

    /**
     * list item or items.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String list(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch sw = new StopWatch();
        sw.start();
        logger.info("doPOST listing items by filter");

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
            if (param.equals(NAME)) {
                req.setAttribute(NAME, param);
                inventories = controller.findAllByProductName(value);
            } else {
                req.setAttribute(DESCRIPTION, param);
                inventories = controller.findAllByDescription(value);
            }
            req.setAttribute(ITEMS, inventories);
        } else {
            inventories = controller.findAll();
            req.setAttribute(ITEMS, inventories);
        }
        sw.stop();
        logger.info("doPOST listing items by filter - time: {}ms", sw.getTime());
        return FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP;
    }

    /**
     * redirect to form create item.
     *
     * @return the string
     */
    private String add() {
        logger.info("doPOST redirecting to form createItem");
        return FORWARD_PAGES_INVENTORY_FORM_CREATE_ITEM_JSP;
    }

    /**
     * update item.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String update(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch sw = new StopWatch();
        sw.start();
        logger.info("doPOST updating inventory item");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Inventory item = controller.findById(Long.parseLong(req.getParameter(ID)));
        item.setQuantity(Integer.parseInt(req.getParameter(QUANTITY)));
        item.setDescription(req.getParameter(DESCRIPTION));
        Product product = Product.getProductFromRequest(req);

        try {
            product = productController.find(product);
        } catch (Exception e) {
            logger.error("Product id {} was not found.", req.getParameter(PRODUCT_ID));
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute(ERROR, "ERROR: Product ID " + req.getParameter(PRODUCT_ID) + " was not found.");
            req.setAttribute(ITEM, item);
            return FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP;
        }

        item.setProduct(product);
        controller.update(item);
        req.setAttribute(ITEM, item);
        sw.stop();
        logger.info("doPOST updating inventory item - time: {}ms", sw.getTime());
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID + item.getId();
    }

    /**
     * update item.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String edit(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST editing item");
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
    private String delete(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST deleting item");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        controller.delete(Long.parseLong(req.getParameter(ID)));
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS;
    }
}
