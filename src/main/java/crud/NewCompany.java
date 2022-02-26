package crud;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewCompany implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		return "forward:formCreateCompany.jsp";
	}
}
