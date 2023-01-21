package servlets.product;

import controllers.CategoryController;
import controllers.ProductController;
import controllers.UserController;
import domain.Category;
import domain.Product;
import domain.User;
import dto.ProductDTO;
import org.apache.commons.lang3.time.StopWatch;
import utils.CurrencyFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static servlets.base.Base.*;

public class ProductServlet extends BaseProduct {

    public static final String ACTION = "action";
    public static final String ERROR_ACTION_CAN_T_BE_NULL = "Error: action can't be null";
    public static final String ACTION_CAN_T_BE_NULL = "Action can't be null";
    public static final String CREATE = "create";
    public static final String LIST = "list";
    public static final String UPDATE = "update";
    public static final String NEW = "new";
    public static final String EDIT = "edit";
    public static final String DELETE = "delete";
    public static final String ERROR_ACTION_NOT_FOUND = "Error: action not found";
    public static final String ACTION_NOT_FOUND = "Action not found";
    public static final String DO_POST_CREATING_PRODUCT = "doPOST creating product";
    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DO_POST_EDITING_PRODUCT = "doPOST editing product";
    public static final String ERROR_ID_CAN_T_BE_NULL = "Error: id can't be null";
    public static final String ID_CAN_T_BE_NULL = "Id can't be null";
    public static final String DO_POST_LISTING_PRODUCTS_BY_FILTER = "doPOST listing products by filter";
    public static final String PARAM = "param";
    public static final String VALUE = "value";
    public static final String DO_POST_REDIRECTING_TO_FORM_CREATE_PRODUCT = "doPOST redirecting to form createProduct";
    public static final String DO_POST_UPDATING_PRODUCT = "doPOST updating product";
    public static final String DO_POST_PRODUCT_UPDATED_IN_MS = "doPOST product updated in {} ms";
    public static final String DO_POST_DELETING_PRODUCT = "doPOST deleting product";
    private final ProductController controller = new ProductController(getEm());
    private final CategoryController categoryController = new CategoryController(getEm());
    public static final String USER_LOGGED = "userLogged";
    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    UserController userController = new UserController(getEm());

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter(ACTION) == null) {
            logger.error(ERROR_ACTION_CAN_T_BE_NULL);
            req.setAttribute(ERROR, ACTION_CAN_T_BE_NULL);
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        switch (req.getParameter(ACTION)) {
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
                return add(req, resp);
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


    public String create(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch sw = new StopWatch();
        sw.start();
        logger.info(DO_POST_CREATING_PRODUCT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY);
        LocalDate parsedDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

        Product product = new Product(req.getParameter(NAME), req.getParameter(DESCRIPTION), req.getParameter(URL), parsedDate, CurrencyFormatter.stringToBigDecimal(req.getParameter(PRICE)));
        product.setUser((User) req.getSession().getAttribute(USER_LOGGED));
        product.setCategory(categoryController.findById(Long.parseLong(req.getParameter(CATEGORY))));
        product = getController().save(product);
        req.setAttribute(PRODUCT, product);
        sw.stop();
        logger.info("doPOST product created in {} ms", sw.getTime());
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + product.getId();
    }

    public String edit(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_EDITING_PRODUCT);
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        String id = req.getParameter(ID);
        if (Objects.isNull(id)) {
            logger.error(ERROR_ID_CAN_T_BE_NULL);
            req.setAttribute(ERROR, ID_CAN_T_BE_NULL);
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = new Product();
        product.setId(Long.parseLong(id));
        product.setUser((User) req.getSession().getAttribute(USER_LOGGED));
        product = getController().find(product);
        req.setAttribute(CATEGORIES, categoryController.findAll());
        req.setAttribute(PRODUCT, new ProductDTO(product));
        return FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP;
    }

    private String list(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_LISTING_PRODUCTS_BY_FILTER);

        Product product = new Product();
        product.setUser((User) req.getSession().getAttribute(USER_LOGGED));

        String id = req.getParameter(ID);
        if (!Objects.isNull(id)) {
            product.setId(Long.parseLong(id));
            product = getController().find(product);
            if (Objects.isNull(product)) {
                return FORWARD_PAGES_NOT_FOUND_JSP;
            }
            req.setAttribute(PRODUCT, product);
            return FORWARD_PAGES_PRODUCT_FORM_LIST_PRODUCT_JSP;
        }

        String param = req.getParameter(PARAM);
        String value = req.getParameter(VALUE);
        if (!Objects.isNull(param) && !Objects.isNull(value)) {
            if (param.equals(NAME)) {
                product.setName(value);
            } else {
                product.setDescription(value);
            }

            if (!Objects.isNull(req.getParameter(CATEGORY)) && !req.getParameter(CATEGORY).isEmpty()) {
                Category categoryFilter = new Category();
                categoryFilter.setId(Long.parseLong(req.getParameter(CATEGORY)));
                product.setCategory(categoryFilter);
            }
        }

        List<Product> products = getController().findAll(product);
        req.setAttribute(PRODUCTS, products);
        req.setAttribute(CATEGORIES, categoryController.findAll());
        return FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP;
    }

    private String add(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_REDIRECTING_TO_FORM_CREATE_PRODUCT);
        req.setAttribute(CATEGORIES, categoryController.findAll());
        return FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP;
    }

    private String update(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch sw = new StopWatch();
        sw.start();
        logger.info(DO_POST_UPDATING_PRODUCT);
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = new Product();
        product.setId(Long.parseLong(req.getParameter(ID)));
        product.setUser((User) req.getSession().getAttribute(USER_LOGGED));

        product = getController().find(product);

        product.setName(req.getParameter(NAME));
        product.setDescription(req.getParameter(DESCRIPTION));
        product.setPrice(CurrencyFormatter.stringToBigDecimal(req.getParameter(PRICE)));
        product.setUrl(req.getParameter(URL));
        product.setCategory(categoryController.findById(Long.parseLong(req.getParameter(CATEGORY))));

        getController().update(product);
        req.setAttribute(PRODUCT, product);
        sw.stop();
        logger.info(DO_POST_PRODUCT_UPDATED_IN_MS, sw.getTime());
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + product.getId();
    }

    private String delete(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_DELETING_PRODUCT);

        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = new Product();
        product.setId(Long.parseLong(req.getParameter(ID)));
        product.setUser((User) req.getSession().getAttribute(USER_LOGGED));
        getController().delete(product);
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS;
    }

    public ProductController getController() {
        if (getEm().getTransaction().isActive()) {
            return controller;
        }

        return new ProductController(getEm());
    }
}
