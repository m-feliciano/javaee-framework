package crud;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.User;
import infra.UserDB;

public class Login implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST valid login");
		String login = req.getParameter("email");
		String password = req.getParameter("password");
		UserDB storage = new UserDB();
		User user = storage.findByEmail(login);
		if (user != null) {
			if (user.isEqual(login, password)) {
				return "redirect:company?action=listAll";
			}
		}
		return "forward:formLogin.jsp";
	}

}