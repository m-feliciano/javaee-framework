package crud.product;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import crud.Action;
import entities.Product;
import infra.ConnectionFactory;
import utils.Validate;

public class ListProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single product");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			ProductController controller = new ProductController(conn);
			Product product = controller.findById(id);
			if (product != null) {
				System.out.println(product);
				req.setAttribute("product", product);
				req.setAttribute("today", new Date());
				return "forward:pages/product/formListProduct.jsp";
			}
		}
		return "forward:pages/notFound.jsp";
	}

}
