package servlets.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteCategory extends BaseCategory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET deleting a category");

        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        controller.delete(Long.parseLong(req.getParameter("id")));
        return "redirect:category?action=ListCategories";
    }

}
