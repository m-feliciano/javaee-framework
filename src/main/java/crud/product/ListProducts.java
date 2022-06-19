package crud.product;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import crud.Action;
import entities.Product;
import infra.ConnectionFactory;

public class ListProducts implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing products");
		Connection conn = new ConnectionFactory().getConnection();
		List<Product> productsList = new ProductController(conn).list();
		req.setAttribute("products", productsList);
		return "forward:pages/product/listProducts.jsp";
	}

}
