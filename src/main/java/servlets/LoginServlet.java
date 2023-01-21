package servlets;

import domain.User;
import org.apache.commons.lang3.time.StopWatch;
import servlets.utils.EncryptDecrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static servlets.base.Base.*;

public class LoginServlet extends BaseLogin {

    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String LOGIN_FORM = "loginForm";
    public static final String ERROR_ACTION_NOT_FOUND = "Error: action not found";
    public static final String ACTION_NOT_FOUND = "Action not found";
    public static final String ACTION = "action";
    public static final String ERROR_ACTION_CAN_T_BE_NULL = "Error: action can't be null";
    public static final String ACTION_CAN_T_BE_NULL = "Action can't be null";
    public static final String DO_POST_REDIRECTING_TO_FORM_LOGIN = "doPOST redirecting to form login";
    public static final String FORWARD_TO_LOGIN_PAGE = "forward to login page";
    public static final String VALIDATE_USER_TO_LOGIN = "Validate user to login";
    public static final String INVALID = "invalid";
    public static final String USER_OR_PASSWORD_INVALID = "User or password invalid.";
    public static final String TIME_TO_VALIDATE_USER_TO_LOGIN_MS = "Time to validate user to login: {}ms";
    public static final String USER_LOGGED = "userLogged";
    public static final String USER_LOGGED_LOGGER = "User logged: {}";
    public static final String TIME_TO_LOGIN_MS = "Time to login: {}ms";
    public static final String LOGOUT_INVALIDATING_SESSION = "logout: invalidating session";
    public static final String LOGOUT_SESSION_IS_NULL = "logout: session is null";
    public static final String LOGOUT_REDIRECTING_TO_FORM_LOGIN = "logout: redirecting to form login";

    /**
     * Execute the action
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
        }
        String request = req.getParameter(ACTION);
        switch (request) {
            case LOGIN -> {
                return login(req, resp);
            }
            case LOGOUT -> {
                return logout(req, resp);
            }
            case LOGIN_FORM -> {
                return loginForm();
            }
            default -> {
                logger.error(ERROR_ACTION_NOT_FOUND);
                req.setAttribute(ERROR, ACTION_NOT_FOUND);
            }
        }
        return FORWARD_PAGES_NOT_FOUND_JSP;
    }

    private String loginForm() {
        logger.info(DO_POST_REDIRECTING_TO_FORM_LOGIN);
        return FORWARD_PAGES_FORM_LOGIN_JSP;
    }

    /**
     * Login.
     *
     * @param req  the req
     * @param resp the resp
     * @return the path to the next page
     */
    private String login(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter(SUCCESS) != null) {
            logger.info(FORWARD_TO_LOGIN_PAGE);
            return FORWARD_PAGES_FORM_LOGIN_JSP;
        }

        StopWatch sw = new StopWatch();
        sw.start();

        logger.info(VALIDATE_USER_TO_LOGIN);

        User user = new User();
        user.setLogin(req.getParameter(EMAIL));
        user.setPassword(EncryptDecrypt.encrypt(req.getParameter(PASSWORD)));
        user = controller.findByLogin(user);
        if (user == null) {
            req.setAttribute(INVALID, USER_OR_PASSWORD_INVALID);
            logger.info(USER_OR_PASSWORD_INVALID);
            req.setAttribute(EMAIL, req.getParameter(EMAIL));
            sw.stop();
            logger.info(TIME_TO_VALIDATE_USER_TO_LOGIN_MS, sw.getTime());
            return FORWARD_PAGES_FORM_LOGIN_JSP;
        }

        HttpSession session = req.getSession();
        session.setAttribute(USER_LOGGED, user);
        logger.info(USER_LOGGED_LOGGER, user.getLogin());

        sw.stop();
        logger.info(TIME_TO_LOGIN_MS, sw.getTime());

        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS;
    }

    /**
     * Logout.
     *
     * @param req  the req
     * @param resp the resp
     * @return the path to login page
     */
    private String logout(HttpServletRequest req, HttpServletResponse resp) {
        logger.info(LOGOUT_INVALIDATING_SESSION);
        HttpSession session = req.getSession();
        session.invalidate();
        logger.info(LOGOUT_SESSION_IS_NULL);
        logger.info(LOGOUT_REDIRECTING_TO_FORM_LOGIN);
        return FORWARD_PAGES_FORM_LOGIN_JSP;
    }
}
