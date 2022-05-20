package crud;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginForm implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		return "forward:pages/formLogin.jsp";
	}

}
