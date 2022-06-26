package servlets;

import domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Login extends BaseLogin {

    /**
     * Login.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("Validate user to login");
        User user = controller.findByLogin(req.getParameter("email"));
        if (user == null || !user.equals(user.getLogin(), req.getParameter("password"))) {
            req.setAttribute("error", "User or password invalid.");
            logger.info("User or password invalid.");
            return "forward:pages/formLogin.jsp";
        }

        HttpSession session = req.getSession();
        session.setAttribute("userLogged", user);
        logger.info("User logged: " + user.getLogin());
        return "redirect:product?action=ListProducts";
    }

}