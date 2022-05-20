package crud.category;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.CategoryController;
import crud.Action;
import entities.Category;
import infra.ConnectionFactory;

public class CreateCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST registering new category");
		String name = req.getParameter("name");
		Connection conn = new ConnectionFactory().getConnection();
		CategoryController controller = new CategoryController(conn);
		Category cat = new Category(name);
		controller.save(cat);

		return "redirect:category?action=ListCategories";

	}

}
