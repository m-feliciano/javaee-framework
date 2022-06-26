package servlets.category;

import controllers.CategoryController;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class DeleteCategory implements Action {

    private final EntityManager em = JPAUtil.getEntityManager();

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("doPOST deleting category");
        if (Objects.isNull(req.getParameter("id"))) {
            req.setAttribute("error", "Category not found");
            return "forward:pages/not-found.jsp";
        }
        Long id = Long.parseLong(req.getParameter("id"));
        CategoryController controller = new CategoryController(em);
        controller.delete(id);
        return "redirect:category?action=ListCategories";
    }

}
