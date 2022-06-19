package crud.product;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import crud.Action;
import infra.ConnectionFactory;
import utils.Validate;

public class DeleteProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST deleting product");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			ProductController controller = new ProductController(conn);
			controller.delete(id);
		}
		return "redirect:product?action=ListProducts";
	}

}
