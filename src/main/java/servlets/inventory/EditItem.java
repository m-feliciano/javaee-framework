package servlets.inventory;

import controllers.InventoryController;
import domain.Inventory;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditItem implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final InventoryController inventoryController = new InventoryController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single inventory item");

		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			Inventory item = inventoryController.findById(id);
			req.setAttribute("item", item);
			return "forward:pages/inventory/formUpdateItem.jsp";
		}

		return "forward:pages/not-found.jsp";
	}

}
