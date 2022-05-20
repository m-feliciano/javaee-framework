package crud.inventory;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.InventoryController;
import crud.Action;
import infra.ConnectionFactory;
import utils.Validate;

public class DeleteItem implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST deleting inventory");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			InventoryController controller = new InventoryController(conn);
			controller.delete(id);
		}
		return "redirect:inventory?action=ListItems";
	}

}
