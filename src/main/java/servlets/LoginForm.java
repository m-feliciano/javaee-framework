package servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginForm extends BaseLogin {

    /**
     * Login.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET redirecting to form login");
        return "forward:pages/formLogin.jsp";
    }

}
