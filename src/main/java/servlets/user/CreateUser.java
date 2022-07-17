package servlets.user;

import domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateUser extends BaseUser {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating a user");
        User user = new User(req.getParameter("email").toLowerCase(), req.getParameter("password"));
        try {
            controller.save(user);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            req.setAttribute("error", e.getMessage());
            return "redirect:product?action=CreateUser";
        }
        logger.info("User {} created successfully", user.getLogin());
        req.setAttribute("success", "User " + user.getLogin() + " created successfully");
        return "forward:pages/formLogin.jsp";
    }

}
