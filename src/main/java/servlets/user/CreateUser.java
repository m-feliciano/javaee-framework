package servlets.user;

import domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateUser extends BaseUser {

    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String EMAIL = "email";
    public static final String ERROR = "error";

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

        if (!this.validatePassword(req)) {
            req.setAttribute(EMAIL, req.getParameter(EMAIL));
            return "forward:pages/user/formCreateUser.jsp";
        }

        User user = new User(req.getParameter(EMAIL).toLowerCase(), req.getParameter(PASSWORD));
        try {
            controller.save(user);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            req.setAttribute(ERROR, e.getMessage());
            return "redirect:product?action=CreateUser";
        }
        logger.info("User {} created successfully", user.getLogin());
        req.setAttribute("success", "User " + user.getLogin() + " created successfully");
        return "forward:pages/formLogin.jsp";
    }

    private boolean validatePassword(HttpServletRequest req) {
        if (req.getParameter(PASSWORD) != null && req.getParameter(CONFIRM_PASSWORD) != null) {
            if (!req.getParameter(PASSWORD).equals(req.getParameter(CONFIRM_PASSWORD))) {
                req.setAttribute(ERROR, "Passwords do not match");
                return false;
            }
        }
        return true;
    }

}
