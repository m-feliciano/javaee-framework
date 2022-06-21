package servlets.inventory;

import servlets.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewItem implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		return "forward:pages/inventory/formCreateItem.jsp";
	}
}
