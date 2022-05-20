package crud.category;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.CategoryController;
import crud.Action;
import entities.Category;
import infra.ConnectionFactory;

public class ListCategories implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing categories");
		Connection conn = new ConnectionFactory().getConnection();
		CategoryController controller = new CategoryController(conn);
		List<Category> productsList = controller.list();
		req.setAttribute("categories", productsList);
		return "forward:category/listCategories.jsp";
	}

}
