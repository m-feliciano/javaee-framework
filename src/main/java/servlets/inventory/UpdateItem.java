package servlets.inventory;

import controllers.InventoryController;
import controllers.ProductController;
import domain.Inventory;
import domain.Product;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public class UpdateItem implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final InventoryController inventoryController = new InventoryController(em);
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing inventory item");

		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			Long productId = Long.parseLong(req.getParameter("productId"));
			int quantity = Integer.parseInt(req.getParameter("quantity"));
			String description = req.getParameter("description");
			Product product = productController.findById(productId);
			Inventory item = inventoryController.findById(id);
			item.setProduct(product);
			item.setQuantity(quantity);
			item.setDescription(description);
			product.setPrice(product.getPrice().multiply(new BigDecimal(quantity)));
			inventoryController.update(item);
		}

		return "redirect:inventory?action=ListItems";
	}

}
