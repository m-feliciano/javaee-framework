package servlets.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListCategories extends BaseCategory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing a category");
        req.setAttribute("categories", controller.findAll());
        return "forward:pages/category/listCategories.jsp";
    }

}
