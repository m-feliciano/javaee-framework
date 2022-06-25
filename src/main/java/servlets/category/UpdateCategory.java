package servlets.category;

import controllers.CategoryController;
import domain.Category;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class UpdateCategory implements Action {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("doPOST editing category");

        if (Objects.isNull(req.getParameter("id"))) {
            req.setAttribute("error", "Category not found");
            return "forward:pages/not-found.jsp";
        }

        String name = req.getParameter("name");
        Long id = Long.parseLong(req.getParameter("id"));
        EntityManager em = JPAUtil.getEntityManager();
        CategoryController controller = new CategoryController(em);
        Category cat = controller.findById(id);
        cat.setName(name);
        controller.update(cat);
        return "redirect:category?action=ListCategories";
    }

}
