package servlets.user;

import domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateUser extends BaseUser {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating user");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }
        User user = controller.findById(Long.parseLong(req.getParameter("id")));
        user.setLogin(req.getParameter("email").toLowerCase());
        user.setPassword(req.getParameter("password"));
        controller.update(user);
        req.setAttribute("user", user);
        return "redirect:user?action=ListUser&id=" + user.getId();
    }

}
