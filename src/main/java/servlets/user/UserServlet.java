package servlets.user;

import domain.User;
import servlets.utils.EncryptDecrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static servlets.base.Base.*;

public class UserServlet extends BaseUser {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter("action") == null) {
            logger.error("Error: action can't be null");
            req.setAttribute(ERROR, "Action can't be null");
            return FORWARD_PAGES_FORM_LOGIN_JSP;
        }
        switch (req.getParameter("action")) {
            case "CreateUser" -> {
                return createUser(req, resp);
            }
            case "ListUser" -> {
                return listUser(req, resp);
            }
            case "UpdateUser" -> {
                return updateUser(req, resp);
            }
            case "NewUser" -> {
                return newUser();
            }
            case "EditUser" -> {
                return editUser(req, resp);
            }
            default -> {
                logger.error("Error: action not found");
                req.setAttribute(ERROR, "Action not found");
            }

        }
        return FORWARD_PAGES_NOT_FOUND_JSP;
    }

    /**
     * Redirect to Create user.
     *
     * @return the string
     */
    public String newUser() {
        logger.info("doGET redirecting to form createUser");
        return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
    }

    /**
     * Redirect to Edit user.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String createUser(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating a user");

        if (!this.validatePassword(req)) {
            req.setAttribute(EMAIL, req.getParameter(EMAIL));
            logger.warn("Passwords do not match - redirecting to register user page");
            return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
        }

        User alreadyExists = controller.findByLogin(req.getParameter(EMAIL));

        if (alreadyExists != null) {
            req.setAttribute(ERROR, "User already exists");
            logger.warn("User already exists - redirecting to register user page");
            return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
        }

        User user = new User(req.getParameter(EMAIL).toLowerCase(), req.getParameter(PASSWORD));
        user.setPassword(EncryptDecrypt.encrypt(user.getPassword()));
        try {
            controller.save(user);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            req.setAttribute(ERROR, e.getMessage());
            return REDIRECT_PRODUCT_ACTION_CREATE_USER;
        }
        logger.info("User {} created successfully", user.getLogin());
        req.setAttribute(SUCCESS, "User " + user.getLogin() + " created successfully");
        return FORWARD_PAGES_FORM_LOGIN_JSP;
    }

    /**
     * Update user.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String updateUser(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating user");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }
        User user = controller.findById(Long.parseLong(req.getParameter(ID)));
        user.setLogin(req.getParameter(EMAIL).toLowerCase());
        user.setPassword(EncryptDecrypt.encrypt(req.getParameter(PASSWORD)));
        controller.update(user);
        req.setAttribute(USER, user);
        return REDIRECT_USER_ACTION_LIST_USER_BY_ID + user.getId();
    }

    /**
     * List user by id.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String listUser(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing a user");

        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        req.setAttribute(USER, controller.findById(Long.parseLong(req.getParameter(ID))));
        return FORWARD_PAGES_USER_FORM_LIST_USER_JSP;
    }

    /**
     * Edit user.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String editUser(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET editing a user");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        req.setAttribute(USER, controller.findById(Long.parseLong(req.getParameter(ID))));
        return FORWARD_PAGES_USER_FORM_UPDATE_USER_JSP;
    }

    /**
     * Validate if the password is the same.
     *
     * @param req the req
     * @return the boolean
     */
    private boolean validatePassword(HttpServletRequest req) {
        if (req.getParameter(PASSWORD) != null && req.getParameter(CONFIRM_PASSWORD) != null) {
            if (!Objects.equals(req.getParameter(PASSWORD), req.getParameter(CONFIRM_PASSWORD))) {
                req.setAttribute(ERROR, "Passwords do not match");
                return false;
            }
        }
        return true;
    }
}
