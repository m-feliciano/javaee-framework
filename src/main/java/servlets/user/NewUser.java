package servlets.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewUser extends BaseUser {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET redirecting to form createUser");
        return "forward:pages/user/formCreateUser.jsp";
    }
}
