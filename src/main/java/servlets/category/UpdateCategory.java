package servlets.category;

import controllers.CategoryController;
import domain.Category;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing category");
		if (Validate.isValid(req, "id")) {
			String name = req.getParameter("name");
			Long id = Long.parseLong(req.getParameter("id"));
			EntityManager em = JPAUtil.getEntityManager();
			CategoryController controller = new CategoryController(em);
			Category cat = controller.findById(id);
			cat.setName(name);
			controller.update(cat);
		}
		return "redirect:category?action=ListCategories";
	}

}
