package servlets.product;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.CurrencyFormatter;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateProduct implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing product");
		Long id = Long.parseLong(req.getParameter("id"));
		String name = req.getParameter("name");
		String description = req.getParameter("description");
		String priceString = req.getParameter("price");
		String url = req.getParameter("url");
		Product product = productController.findById(id);
		product.setName(name);
		product.setDescription(description);
		product.setPrice(CurrencyFormatter.stringToBigDecimal(priceString));
		product.setUrl(url);
		productController.update(product);
		return "redirect:product?action=ListProducts";
	}

}
