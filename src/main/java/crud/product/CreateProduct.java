package crud.product;

import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import crud.Action;
import dao.ProductDAO;
import entities.Product;
import infra.ConnectionFactory;

public class CreateProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST registering new product");
		String name = req.getParameter("name");
		String descriprion = req.getParameter("description");
		String priceString = req.getParameter("price");
		BigDecimal price = new BigDecimal(priceString);

		Connection conn = new ConnectionFactory().getConnection();
		ProductDAO dao = new ProductDAO(conn);
		Product product = new Product(name, descriprion, price);
		dao.save(product);

		return "redirect:product?action=ListProducts";

	}

}
