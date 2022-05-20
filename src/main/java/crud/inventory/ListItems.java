package crud.inventory;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.InventoryController;
import crud.Action;
import dto.InventoryDTO;
import infra.ConnectionFactory;

public class ListItems implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing inventory items");
		Connection conn = new ConnectionFactory().getConnection();
		InventoryController controller = new InventoryController(conn);
		List<InventoryDTO> items = controller.list();
		req.setAttribute("items", items);
		return "forward:pages/inventory/listItems.jsp";
	}

}
