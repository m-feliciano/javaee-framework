package servlets.category;

import domain.Category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static servlets.base.Base.*;

public class CategoryServlet extends BaseCategory {

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
            case "CreateCategory" -> {
                return createCategory(req, resp);
            }
            case "ListCategories" -> {
                return listCategories(req, resp);
            }
            case "ListCategory" -> {
                return listCategory(req, resp);
            }
            case "UpdateCategory" -> {
                return updateCategory(req, resp);
            }
            case "NewCategory" -> {
                return newCategory();
            }
            case "EditCategory" -> {
                return editCategory(req, resp);
            }
            case "DeleteCategory" -> {
                return deleteCategory(req, resp);
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

    private String updateCategory(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating category");
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

    private String newCategory() {
        logger.info("doGET redirecting to form createCategory");
        return FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP;
    }

    /**
     * list categories.
     *
     * @return the string
     */

    private String listCategories(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing a category");
        req.setAttribute(CATEGORIES, controller.findAll());
        return FORWARD_PAGES_CATEGORY_LIST_CATEGORIES_JSP;
    }

    /**
     * list category by id.
     *
     * @return the string
     */

    private String listCategory(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing a category");

        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        req.setAttribute(CATEGORY, controller.findById(Long.parseLong(req.getParameter(ID))));
        return FORWARD_PAGES_CATEGORY_FORM_LIST_CATEGORY_JSP;
    }

    /**
     * edit category by id.
     *
     * @return the string
     */

    private String editCategory(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET editing a category");
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

    private String deleteCategory(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET deleting a category");

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

    private String createCategory(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating a category");
        Category cat = new Category(req.getParameter(NAME));
        controller.save(cat);
        return REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID + cat.getId();
    }
}
