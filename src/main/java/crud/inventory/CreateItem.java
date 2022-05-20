package crud.inventory;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.InventoryController;
import crud.Action;
import entities.Inventory;
import infra.ConnectionFactory;

public class CreateItem implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST registering new inventory");
		int productId = Integer.parseInt(req.getParameter("productId"));
		int categoryId = Integer.parseInt(req.getParameter("categoryId"));
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		String description = req.getParameter("description");

		Connection conn = new ConnectionFactory().getConnection();
		InventoryController controller = new InventoryController(conn);
		Inventory item = new Inventory(productId, categoryId, quantity, description);
		controller.save(item);

		System.out.println("item: "+ item);
		return "redirect:inventory?action=ListItems";
	}

}
