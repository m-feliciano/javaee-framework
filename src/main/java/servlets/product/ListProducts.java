package servlets.product;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ListProducts implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing products");
		List<Product> productsList = productController.findAll();
		req.setAttribute("products", productsList);

		return "forward:pages/product/listProducts.jsp";
	}

}
