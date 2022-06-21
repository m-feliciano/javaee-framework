package servlets;

import controllers.UserController;
import domain.User;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Login implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final UserController userController = new UserController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST valid login");

		String login = req.getParameter("email");
		String password = req.getParameter("password");
		User user = userController.findByLogin(login);
		if (user != null && user.equals(login, password)) {
			HttpSession session = req.getSession();
			session.setAttribute("userLogged", user);
			return "redirect:product?action=ListProducts";
		}

		req.setAttribute("error", "User or password invalid.");
		return "forward:pages/formLogin.jsp";
	}

}