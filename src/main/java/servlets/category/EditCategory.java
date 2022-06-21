package servlets.category;

import controllers.CategoryController;
import domain.Category;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single category");

		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			EntityManager em = JPAUtil.getEntityManager();
			CategoryController controller = new CategoryController(em);
			Category cat = controller.findById(id);
			req.setAttribute("category", cat);
			return "forward:pages/category/formUpdateCategory.jsp";
		}

		return "forward:pages/not-found.jsp";
	}

}
