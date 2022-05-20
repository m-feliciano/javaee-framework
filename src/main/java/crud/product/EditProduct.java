package crud.product;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import crud.Action;
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
			ProductController controller = new ProductController(conn);
			Product product = controller.findById(id);
			ProductDTO dto = new ProductDTO(product);
			req.setAttribute("product", dto);
			return "forward:pages/product/formUpdateProduct.jsp";
		}
		return "forward:pages/notFound.jsp";
	}

}
