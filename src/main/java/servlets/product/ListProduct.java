package servlets.product;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListProduct implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single product");

		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			Product product = productController.findById(id);
			if (product != null) {
				req.setAttribute("product", product);
				return "forward:pages/product/formListProduct.jsp";
			}
		}

		return "forward:pages/not-found.jsp";
	}

}
