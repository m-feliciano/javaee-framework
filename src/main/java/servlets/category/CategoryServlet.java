package servlets.category;

import controllers.CategoryController;
import domain.Category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static servlets.base.Base.*;

public class CategoryServlet extends BaseCategory {

    public static final String CREATE = "create";
    public static final String LIST = "list";
    public static final String UPDATE = "update";
    public static final String NEW = "new";
    public static final String EDIT = "edit";
    public static final String DELETE = "delete";
    public static final String DO_POST_UPDATING_CATEGORY = "doPOST updating category";
    public static final String DO_POST_REDIRECTING_TO_FORM_CREATE_CATEGORY = "doPOST redirecting to form createCategory";
    public static final String DO_POST_LISTING_A_CATEGORY = "doPOST listing a category";
    public static final String DO_POST_EDITING_A_CATEGORY = "doPOST editing a category";
    public static final String DO_POST_DELETING_A_CATEGORY = "doPOST deleting a category";
    public static final String DO_POST_CREATING_A_CATEGORY = "doPOST creating a category";
    public static final String ACTION = "action";
    private final CategoryController controller = new CategoryController(getEm());

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter(ACTION) == null) {
            logger.error("Error: action can't be null");
            req.setAttribute(ERROR, "Action can't be null");
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
                return add();
            }
            case EDIT -> {
                return edit(req, resp);
            }
            case DELETE -> {
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
     * update category.
     *
     * @return the string
     */

    private String update(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_UPDATING_CATEGORY);
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }
        Category cat = controller.findById(Long.parseLong(req.getParameter(ID)));
        cat.setName(req.getParameter(NAME));
        controller.update(cat);
        req.setAttribute(CATEGORY, cat);
        return REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID + cat.getId();
    }

    /**
     * redirect to new category page.
     *
     * @return the string
     */

    private String add() {
        logger.info(DO_POST_REDIRECTING_TO_FORM_CREATE_CATEGORY);
        return FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP;
    }

    /**
     * list category by id.
     *
     * @return the string
     */

    private String list(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_LISTING_A_CATEGORY);

        String id = req.getParameter(ID);
        if (!Objects.isNull(id)) {
            req.setAttribute(CATEGORY, controller.findById(Long.parseLong(req.getParameter(ID))));
            return FORWARD_PAGES_CATEGORY_FORM_LIST_CATEGORY_JSP;
        }

        req.setAttribute(CATEGORIES, controller.findAll());
        return FORWARD_PAGES_CATEGORY_LIST_CATEGORIES_JSP;
    }

    /**
     * edit category by id.
     *
     * @return the string
     */

    private String edit(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_EDITING_A_CATEGORY);
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        req.setAttribute(CATEGORY, controller.findById(Long.parseLong(req.getParameter(ID))));
        return FORWARD_PAGES_CATEGORY_FORM_UPDATE_CATEGORY_JSP;
    }

    /**
     * delete category by id.
     *
     * @return the string
     */

    private String delete(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_DELETING_A_CATEGORY);

        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        controller.delete(Long.parseLong(req.getParameter(ID)));
        return REDIRECT_CATEGORY_ACTION_LIST_CATEGORIES;
    }

    /**
     * create category.
     *
     * @return the string
     */

    private String create(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(DO_POST_CREATING_A_CATEGORY);
        Category cat = new Category(req.getParameter(NAME));
        controller.save(cat);
        return REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID + cat.getId();
    }
}
