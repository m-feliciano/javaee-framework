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
    public static final String ACTION = "action";
    public static final String ERROR_ACTION_CAN_T_BE_NULL = "Error: action can't be null";
    public static final String ACTION_CAN_T_BE_NULL = "Action can't be null";
    public static final String ACTION1 = "action";
    public static final String CREATE = "create";
    public static final String LIST = "list";
    public static final String UPDATE = "update";
    public static final String NEW = "new";
    public static final String EDIT = "edit";
    public static final String DELETE = "delete";
    public static final String ERROR_ACTION_NOT_FOUND = "Error: action not found";
    public static final String ACTION_NOT_FOUND = "Action not found";
    public static final String DO_POST_CREATING_A_INVENTORY_ITEM = "doPOST creating a inventory item";
    public static final String DO_POST_CREATING_A_INVENTORY_ITEM_TOOK_MS = "doPOST creating a inventory item took {} ms";
    public static final String DO_POST_LISTING_ITEMS_BY_FILTER = "doPOST listing items by filter";
    public static final String PARAM = "param";
    public static final String VALUE = "value";
    public static final String DO_POST_LISTING_ITEMS_BY_FILTER_TIME_MS = "doPOST listing items by filter - time: {}ms";
    public static final String DO_POST_REDIRECTING_TO_FORM_CREATE_ITEM = "doPOST redirecting to form createItem";
    public static final String DO_POST_UPDATING_INVENTORY_ITEM = "doPOST updating inventory item";
    public static final String PRODUCT_ID_WAS_NOT_FOUND = "Product id {} was not found.";
    public static final String DO_POST_UPDATING_INVENTORY_ITEM_TIME_MS = "doPOST updating inventory item - time: {}ms";
    public static final String DO_POST_EDITING_ITEM = "doPOST editing item";
    public static final String DO_POST_DELETING_ITEM = "doPOST deleting item";
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
        if (req.getParameter(ACTION) == null) {
            logger.error(ERROR_ACTION_CAN_T_BE_NULL);
            req.setAttribute(ERROR, ACTION_CAN_T_BE_NULL);
        }
        switch (req.getParameter(ACTION1)) {
            case CREATE -> {
                return create(req, resp);
            }
            case LIST -> {
                return list(req, resp);
            }
            case UPDATE -> {
                return update(req, resp);
            }
            case NEW -> {
                return add();
            }
            case EDIT -> {
                return edit(req, resp);
            }
            case DELETE -> {
                return delete(req, resp);
            }
            default -> {
                logger.error(ERROR_ACTION_NOT_FOUND);
                req.setAttribute(ERROR, ACTION_NOT_FOUND);
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
        logger.info(DO_POST_CREATING_A_INVENTORY_ITEM);
        Product product = Product.getProductFromRequest(req);
        product = productController.find(product);
        int quantity = Integer.parseInt(req.getParameter(QUANTITY));
        Inventory item = new Inventory(product, quantity, req.getParameter(DESCRIPTION));
        controller.save(item);
        req.setAttribute(ITEM, item);
        stopWatch.stop();
        logger.info(DO_POST_CREATING_A_INVENTORY_ITEM_TOOK_MS, stopWatch.getTime());
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
        logger.info(DO_POST_LISTING_ITEMS_BY_FILTER);

        String id = req.getParameter(ID);
        if (!Objects.isNull(id)) {
            Inventory inventory = controller.findById(Long.parseLong(id));
            if (Objects.isNull(inventory)) {
                return FORWARD_PAGES_NOT_FOUND_JSP;
            }

            req.setAttribute(ITEM, inventory);
            return FORWARD_PAGES_INVENTORY_FORM_LIST_ITEM_JSP;
        }

        String param = req.getParameter(PARAM);
        String value = req.getParameter(VALUE);
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
        logger.info(DO_POST_LISTING_ITEMS_BY_FILTER_TIME_MS, sw.getTime());
        return FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP;
    }

    /**
     * redirect to form create item.
     *
     * @return the string
     */
    private String add() {
        logger.info(DO_POST_REDIRECTING_TO_FORM_CREATE_ITEM);
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
        logger.info(DO_POST_UPDATING_INVENTORY_ITEM);
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Inventory item = controller.findById(Long.parseLong(req.getParameter(ID)));
        item.setQuantity(Integer.parseInt(req.getParameter(QUANTITY)));
        item.setDescription(req.getParameter(DESCRIPTION));
        Product product = Product.getProductFromRequest(req);


        product = productController.find(product);
        if (Objects.isNull(product)) {
            logger.error(PRODUCT_ID_WAS_NOT_FOUND, req.getParameter(PRODUCT_ID));
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute(ERROR, "ERROR: Product ID " + req.getParameter(PRODUCT_ID) + " was not found.");
            req.setAttribute(ITEM, item);
            return FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP;
        }

        item.setProduct(product);
        controller.update(item);
        req.setAttribute(ITEM, item);
        sw.stop();
        logger.info(DO_POST_UPDATING_INVENTORY_ITEM_TIME_MS, sw.getTime());
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
        logger.info(DO_POST_EDITING_ITEM);
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
        logger.info(DO_POST_DELETING_ITEM);
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        controller.delete(Long.parseLong(req.getParameter(ID)));
        return REDIRECT_INVENTORY_ACTION_LIST_ITEMS;
    }
}
