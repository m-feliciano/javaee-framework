package servlets.category;

import controllers.CategoryController;
import domain.Category;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST registering new category");
		String name = req.getParameter("name");
		EntityManager em = JPAUtil.getEntityManager();
		CategoryController controller = new CategoryController(em);
		Category cat = new Category(name);
		controller.save(cat);
		return "redirect:category?action=ListCategories";
	}

}
