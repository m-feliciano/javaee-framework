package servlets.category;

import servlets.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewCategory implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		return "forward:pages/category/formCreateCategory.jsp";
	}
}
