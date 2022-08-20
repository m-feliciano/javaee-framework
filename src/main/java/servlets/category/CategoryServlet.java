package servlets.category;

import controllers.CategoryController;
import domain.Category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static servlets.base.Base.*;

public class CategoryServlet extends BaseCategory {

    private final CategoryController controller = new CategoryController(em);
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
     * update category.
     *
     * @return the string
     */

    private String update(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST updating category");
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
        logger.info("doPOST redirecting to form createCategory");
        return FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP;
    }

    /**
     * list category by id.
     *
     * @return the string
     */

    private String list(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST listing a category");

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
        logger.info("doPOST editing a category");
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
        logger.info("doPOST deleting a category");

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
        logger.info("doPOST creating a category");
        Category cat = new Category(req.getParameter(NAME));
        controller.save(cat);
        return REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID + cat.getId();
    }
}
