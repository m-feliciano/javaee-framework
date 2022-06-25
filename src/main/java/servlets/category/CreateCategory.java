package servlets.category;

import controllers.CategoryController;
import domain.Category;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateCategory implements Action {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {

        System.out.println("doPOST registering new category");
        String name = req.getParameter("name");
        EntityManager em = JPAUtil.getEntityManager();
        CategoryController controller = new CategoryController(em);
        Category cat = new Category(name);
        controller.save(cat);
        return "redirect:category?action=ListCategories";
    }

}
