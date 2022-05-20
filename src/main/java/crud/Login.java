package crud;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		if (user != null && user.isEqual(login, password)) {
			HttpSession session = req.getSession();
			session.setAttribute("userLogged", user);
			return "redirect:product?action=ListProducts";
		}
		req.setAttribute("error", "User or password invalid.");
		return "forward:formLogin.jsp";
	}

}