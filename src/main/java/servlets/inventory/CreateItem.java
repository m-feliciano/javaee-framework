package servlets.inventory;

import controllers.InventoryController;
import controllers.ProductController;
import domain.Inventory;
import domain.Product;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public class CreateItem implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);
	private final InventoryController inventoryController = new InventoryController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST registering new inventory");

		Long productId = Long.parseLong(req.getParameter("productId"));
		Product product = productController.findById(productId);
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		String description = req.getParameter("description");
		Inventory item = new Inventory(product, quantity, description, product.getPrice().multiply(new BigDecimal(quantity)));
		inventoryController.save(item);

		System.out.println("item: " + item);
		return "redirect:inventory?action=ListItems";
	}

}
