package crud.product;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import crud.Action;
import entities.Product;
import infra.ConnectionFactory;
import utils.Validate;

public class ListProductsByName implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing products by name");
		if (Validate.isValid(req, "name")) {
			String name = req.getParameter("name");
			Connection conn = new ConnectionFactory().getConnection();
			ProductController controller = new ProductController(conn);
			List<Product> list = controller.findAllByName(name);
			req.setAttribute("products", list);
		}
		return "forward:pages/product/listProducts.jsp";
	}

}
