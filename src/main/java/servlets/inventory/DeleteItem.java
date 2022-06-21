package servlets.inventory;

import controllers.InventoryController;
import servlets.Action;
import utils.JPAUtil;
import utils.Validate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteItem implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final InventoryController inventoryController = new InventoryController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST deleting inventory");

		if (Validate.isValid(req, "id")) {
			Long id = Long.parseLong(req.getParameter("id"));
			inventoryController.delete(id);
		}

		return "redirect:inventory?action=ListItems";
	}

}
