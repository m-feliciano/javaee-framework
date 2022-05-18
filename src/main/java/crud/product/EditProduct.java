package crud.product;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import crud.Action;
import dao.ProductDAO;
import dto.ProductDTO;
import entities.Product;
import infra.ConnectionFactory;
import utils.Validate;

public class EditProduct implements Action {

	@Override
		public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single product");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			ProductDAO dao = new ProductDAO(conn);
			Product product = dao.findById(id);
			ProductDTO dto = new ProductDTO(product);			
			req.setAttribute("product", dto);
			return "forward:formUpdateProduct.jsp";
		}
		return "forward:productNotFound.jsp";
	}

}
