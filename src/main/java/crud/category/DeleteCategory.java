package crud.category;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.CategoryController;
import crud.Action;
import infra.ConnectionFactory;
import utils.Validate;

public class DeleteCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST deleting category");
		if (Validate.isValid(req, "id")) {		
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			CategoryController controller = new CategoryController(conn);
			controller.delete(id);
		}
		return "redirect:category?action=ListCategories";
	}

}
