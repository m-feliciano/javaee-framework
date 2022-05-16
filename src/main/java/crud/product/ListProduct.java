package crud.product;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import crud.Action;
import dao.ProductDAO;
import entities.Product;
import infra.ConnectionFactory;
import services.Validate;

public class ListProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single product");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			ProductDAO dao = new ProductDAO(conn);
			Product product = dao.findById(id);
			if (product != null) {
				System.out.println(product);
				req.setAttribute("product", product);
				req.setAttribute("today", new Date());
				return "forward:formListProduct.jsp";
			}
		}
		return "forward:productNotFound.jsp";
	}

}
