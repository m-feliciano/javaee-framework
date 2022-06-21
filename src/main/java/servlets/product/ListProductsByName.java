package servlets.product;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ListProductsByName implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing products by name");
		if (Validate.isValid(req, "name")) {
			String name = req.getParameter("name");
			List<Product> list = productController.findAllByName(name);
			req.setAttribute("products", list);
		}

		return "forward:pages/product/listProducts.jsp";
	}

}
