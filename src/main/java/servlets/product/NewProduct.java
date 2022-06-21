package servlets.product;

import servlets.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewProduct implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		return "forward:pages/product/formCreateProduct.jsp";
	}
}
