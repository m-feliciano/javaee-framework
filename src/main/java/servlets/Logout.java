package servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Logout extends BaseLogin {

    /**
     * Logout.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("logout: invalidating session");
        HttpSession session = req.getSession();
        session.invalidate();
        logger.info("logout: session is null");
        logger.info("logout: redirecting to form login");
        return "forward:pages/formLogin.jsp";
    }

}