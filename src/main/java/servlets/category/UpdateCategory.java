package servlets.category;

import domain.Category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateCategory extends BaseCategory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating category");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }
        Category cat = controller.findById(Long.parseLong(req.getParameter("id")));
        cat.setName(req.getParameter("name"));
        controller.update(cat);
        req.setAttribute("category", cat);
        return "redirect:category?action=ListCategory&id=" + cat.getId();
    }

}
