package crud.product;

import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import crud.Action;
import dao.ProductDAO;
import entities.Product;
import infra.ConnectionFactory;
import services.Validate;

public class UpdateProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing product");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));			
			String name = req.getParameter("name");
			String descriprion = req.getParameter("description");
			String priceString = req.getParameter("price");

			Connection conn = new ConnectionFactory().getConnection();
			ProductDAO dao = new ProductDAO(conn);
			Product product = dao.findById(id);			
			product.setName(name);
			product.setDescription(descriprion);
			product.setPrice(new BigDecimal(priceString));			
			dao.update(product);
//			req.setAttribute("product", storage);
		}
		return "redirect:product?action=ListProducts";
	}

}
