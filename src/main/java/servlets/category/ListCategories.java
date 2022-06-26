package servlets.category;

import controllers.CategoryController;
import domain.Category;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ListCategories implements Action {


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
        System.out.println("doGET listing categories");
        CategoryController controller = new CategoryController(em);
        List<Category> list = controller.findAll();
        req.setAttribute("categories", list);
        return "forward:pages/category/listCategories.jsp";
    }

}
