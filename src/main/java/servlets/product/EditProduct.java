package servlets.product;

import controllers.ProductController;
import domain.Product;
import dto.ProductDTO;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditProduct implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single product");

		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			Product product = productController.findById(id);
			ProductDTO dto = new ProductDTO(product);
			req.setAttribute("product", dto);
			return "forward:pages/product/formUpdateProduct.jsp";
		}

		return "forward:pages/not-found.jsp";
	}

}
