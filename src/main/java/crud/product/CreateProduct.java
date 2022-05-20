package crud.product;

import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import crud.Action;
import entities.Product;
import infra.ConnectionFactory;
import utils.CurrencyFormatter;

public class CreateProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST registering new product");
		String name = req.getParameter("name");
		String descriprion = req.getParameter("description");
		String priceString = req.getParameter("price");
		BigDecimal price = CurrencyFormatter.stringToBigDecimal(priceString);
		Connection conn = new ConnectionFactory().getConnection();
		ProductController controller = new ProductController(conn);
		Product product = new Product(name, descriprion, price);
		controller.save(product);
		return "redirect:product?action=ListProducts";

	}

}
