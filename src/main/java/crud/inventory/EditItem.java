package crud.inventory;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.InventoryController;
import crud.Action;
import entities.Inventory;
import infra.ConnectionFactory;
import utils.Validate;

public class EditItem implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single inventory item");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Connection conn = new ConnectionFactory().getConnection();
			InventoryController controller = new InventoryController(conn);
			Inventory item = controller.findById(id);
			req.setAttribute("item", item);
			return "forward:pages/inventory/formUpdateItem.jsp";
		}
		return "forward:pages/not-found.jsp";
	}

}
