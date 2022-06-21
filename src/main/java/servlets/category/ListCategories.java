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

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing categories");

		EntityManager em = JPAUtil.getEntityManager();
		CategoryController controller = new CategoryController(em);
		List<Category> list = controller.findAll();
		req.setAttribute("categories", list);
		return "forward:pages/category/listCategories.jsp";
	}

}
