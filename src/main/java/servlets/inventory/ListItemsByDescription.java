package servlets.inventory;

import controllers.InventoryController;
import domain.Inventory;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ListItemsByDescription implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final InventoryController inventoryController = new InventoryController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing inventory items by description");

		if (Validate.isValid(req, "description")) {
			String name = req.getParameter("description");
			List<Inventory> items = inventoryController.findByDescription(name);

			if (items != null) {
				req.setAttribute("items", items);
				return "forward:pages/inventory/listItems.jsp";
			}
		}

		return "forward:pages/not-found.jsp";
	}

}
