package servlets.category;

import domain.Category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateCategory extends BaseCategory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating a category");
        Category cat = new Category(req.getParameter("name"));
        controller.save(cat);
        return "redirect:category?action=ListCategory&id=" + cat.getId();
    }

}
