package crud.category;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.CategoryController;
import crud.Action;
import entities.Category;
import infra.ConnectionFactory;
import utils.Validate;

public class UpdateCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing category");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			String name = req.getParameter("name");

			Connection conn = new ConnectionFactory().getConnection();
			CategoryController controller = new CategoryController(conn);
			Category cat = controller.findById(id);
			cat.setName(name);
			controller.update(cat);
		}
		return "redirect:category?action=ListCategories";
	}

}
