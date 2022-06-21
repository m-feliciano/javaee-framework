package servlets.category;

import controllers.CategoryController;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST deleting category");
		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			EntityManager em = JPAUtil.getEntityManager();
			CategoryController controller = new CategoryController(em);
			controller.delete(id);
		}

		return "redirect:category?action=ListCategories";
	}

}
