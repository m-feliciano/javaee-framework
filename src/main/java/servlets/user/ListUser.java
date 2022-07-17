package servlets.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListUser extends BaseUser {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing a user");

        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        req.setAttribute("user", controller.findById(Long.parseLong(req.getParameter("id"))));
        return "forward:pages/user/formListUser.jsp";
    }

}
