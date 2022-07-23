package servlets;

import domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Login extends BaseLogin {
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    /**
     * Login.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {

        if (req.getParameter("success") != null) {
            logger.info("forward to login page");
            return "forward:pages/formLogin.jsp";
        }

        logger.info("Validate user to login");
        User user = controller.findByLogin(req.getParameter(EMAIL));
        if (user == null || !user.equals(user.getLogin(), req.getParameter(PASSWORD))) {
            req.setAttribute("invalid", "User or password invalid.");
            logger.info("User or password invalid.");
            return "forward:pages/formLogin.jsp";
        }

        HttpSession session = req.getSession();
        session.setAttribute("userLogged", user.getLogin());
        logger.info("User logged: " + user.getLogin());
        return "redirect:product?action=ListProducts";
    }

}