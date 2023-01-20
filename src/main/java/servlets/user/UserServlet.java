package servlets.user;

import controllers.UserController;
import domain.User;
import servlets.utils.EncryptDecrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static servlets.base.Base.*;
import static servlets.product.ProductServlet.USER_LOGGED;

public class UserServlet extends BaseUser {

    private final UserController controller = new UserController(getEm());

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
            case "create" -> {
                return create(req, resp);
            }
            case "list" -> {
                return list(req, resp);
            }
            case "update" -> {
                return update(req, resp);
            }
            case "new" -> {
                return add();
            }
            case "edit" -> {
                return edit(req, resp);
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
    public String add() {
        logger.info("doPOST redirecting to form createUser");
        return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
    }

    /**
     * Redirect to Edit user.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String create(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST creating a user");

        if (!this.validatePassword(req)) {
            req.setAttribute(EMAIL, req.getParameter(EMAIL));
            logger.warn("Passwords do not match - redirecting to register user page");
            return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
        }

        User user = new User();
        user.setLogin(req.getParameter(EMAIL).toLowerCase());
        user = getController().find(user);

        if (user != null) {
            req.setAttribute(ERROR, "User already exists");
            logger.warn("User {} already exists - redirecting to register user page", user.getLogin());
            return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
        }

        user = new User(req.getParameter(EMAIL).toLowerCase(), EncryptDecrypt.encrypt(req.getParameter(PASSWORD)));

        try {
            user = getController().save(user);
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
    private String update(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST updating user");

        User user = (User) req.getSession().getAttribute(USER_LOGGED);
        user.setLogin(req.getParameter(EMAIL).toLowerCase());
        user.setImgUrl(req.getParameter("imgUrl"));

        if (!this.validatePassword(req)) {
            req.setAttribute(USER, user);
            logger.warn("Passwords do not match - redirecting to list user page");
            req.setAttribute("invalid", "Passwords do not match");
            return FORWARD_PAGES_USER_FORM_LIST_USER_JSP;
        }

        user.setPassword(EncryptDecrypt.encrypt(req.getParameter(CONFIRM_PASSWORD)));

        user = getController().update(user);
        req.setAttribute(USER, user);
        return REDIRECT_USER_ACTION_LIST_USER_BY_ID + user.getId();
    }

    /**
     * List user by session.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String list(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST listing a user");
        req.setAttribute(USER, (User) req.getSession().getAttribute(USER_LOGGED));
        return FORWARD_PAGES_USER_FORM_LIST_USER_JSP;
    }

    /**
     * Edit user.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    private String edit(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST editing a user");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        req.setAttribute(USER, getController().findById(Long.parseLong(req.getParameter(ID))));
        return FORWARD_PAGES_USER_FORM_UPDATE_USER_JSP;
    }

    /**
     * Validate if the password is the same.
     *
     * @param req the req
     * @return the boolean
     */
    private boolean validatePassword(HttpServletRequest req) {
        boolean valid = false;
        if (req.getParameter(PASSWORD) != null && req.getParameter(CONFIRM_PASSWORD) != null) {
            if (Objects.equals(req.getParameter(PASSWORD), req.getParameter(CONFIRM_PASSWORD))) {
                valid = true;
            }

            if (Objects.equals(EncryptDecrypt.decrypt(req.getParameter(PASSWORD)), req.getParameter(CONFIRM_PASSWORD))) {
                valid = true;
            }
        }

        if (!valid) {
            req.setAttribute(ERROR, "Passwords do not match");
        }

        return valid;
    }

    public UserController getController() {
        if (getEm().getTransaction().isActive()) {
            return controller;
        }

        return new UserController(getEm());
    }
}
