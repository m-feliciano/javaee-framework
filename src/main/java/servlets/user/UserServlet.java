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

    public static final String ACTION = "action";
    public static final String ERROR_ACTION_CAN_T_BE_NULL = "Error: action can't be null";
    public static final String ACTION_CAN_T_BE_NULL = "Action can't be null";
    public static final String CREATE = "create";
    public static final String LIST = "list";
    public static final String UPDATE = "update";
    public static final String NEW = "new";
    public static final String EDIT = "edit";
    public static final String ERROR_ACTION_NOT_FOUND = "Error: action not found";
    public static final String ACTION_NOT_FOUND = "Action not found";
    public static final String DO_POST_REDIRECTING_TO_FORM_CREATE_USER = "doPOST redirecting to form createUser";
    public static final String DO_POST_CREATING_A_USER = "doPOST creating a user";
    public static final String PASSWORDS_DO_NOT_MATCH_REDIRECTING_TO_REGISTER_USER_PAGE = "Passwords do not match - redirecting to register user page";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_ALREADY_EXISTS_REDIRECTING_TO_REGISTER_USER_PAGE = "User {} already exists - redirecting to register user page";
    public static final String USER_CREATED_SUCCESSFULLY = "User {} created successfully";
    public static final String DO_POST_UPDATING_USER = "doPOST updating user";
    public static final String PASSWORDS_DO_NOT_MATCH_REDIRECTING_TO_LIST_USER_PAGE = "Passwords do not match - redirecting to list user page";
    public static final String INVALID = "invalid";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
    public static final String DO_POST_LISTING_A_USER = "doPOST listing a user";
    public static final String DO_POST_EDITING_A_USER = "doPOST editing a user";
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
        if (req.getParameter(ACTION) == null) {
            logger.error(ERROR_ACTION_CAN_T_BE_NULL);
            req.setAttribute(ERROR, ACTION_CAN_T_BE_NULL);
            return FORWARD_PAGES_FORM_LOGIN_JSP;
        }
        switch (req.getParameter(ACTION)) {
            case CREATE -> {
                return create(req, resp);
            }
            case LIST -> {
                return list(req, resp);
            }
            case UPDATE -> {
                return update(req, resp);
            }
            case NEW -> {
                return add();
            }
            case EDIT -> {
                return edit(req, resp);
            }
            default -> {
                logger.error(ERROR_ACTION_NOT_FOUND);
                req.setAttribute(ERROR, ACTION_NOT_FOUND);
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
        logger.info(DO_POST_REDIRECTING_TO_FORM_CREATE_USER);
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
        logger.info(DO_POST_CREATING_A_USER);

        if (!this.validatePassword(req)) {
            req.setAttribute(EMAIL, req.getParameter(EMAIL));
            logger.warn(PASSWORDS_DO_NOT_MATCH_REDIRECTING_TO_REGISTER_USER_PAGE);
            return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
        }

        User user = new User();
        user.setLogin(req.getParameter(EMAIL).toLowerCase());
        user = getController().find(user);

        if (user != null) {
            req.setAttribute(ERROR, USER_ALREADY_EXISTS);
            logger.warn(USER_ALREADY_EXISTS_REDIRECTING_TO_REGISTER_USER_PAGE, user.getLogin());
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
        logger.info(USER_CREATED_SUCCESSFULLY, user.getLogin());
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
        logger.info(DO_POST_UPDATING_USER);

        User user = (User) req.getSession().getAttribute(USER_LOGGED);
        user.setLogin(req.getParameter(EMAIL).toLowerCase());
        user.setImgUrl(req.getParameter("imgUrl"));

        if (!this.validatePassword(req)) {
            req.setAttribute(USER, user);
            logger.warn(PASSWORDS_DO_NOT_MATCH_REDIRECTING_TO_LIST_USER_PAGE);
            req.setAttribute(INVALID, PASSWORDS_DO_NOT_MATCH);
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
        logger.info(DO_POST_LISTING_A_USER);
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
        logger.info(DO_POST_EDITING_A_USER);
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
            req.setAttribute(ERROR, PASSWORDS_DO_NOT_MATCH);
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
