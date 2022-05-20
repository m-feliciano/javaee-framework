package crud.inventory;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.InventoryController;
import crud.Action;
import entities.Inventory;
import infra.ConnectionFactory;
import utils.Validate;

public class UpdateItem implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing inventory item");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			int productId = Integer.parseInt(req.getParameter("productId"));
			int categoryId = Integer.parseInt(req.getParameter("categoryId"));
			int quantity = Integer.parseInt(req.getParameter("quantity"));
			String description = req.getParameter("description");

			Connection conn = new ConnectionFactory().getConnection();
			InventoryController controller = new InventoryController(conn);
			Inventory item = controller.findById(id);
			item.setProductId(productId);
			item.setCategoryId(categoryId);
			item.setQuantity(quantity);
			item.setDescription(description);
			controller.update(item);
		}
		return "redirect:inventory?action=ListItems";
	}

}
