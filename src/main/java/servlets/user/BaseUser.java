package servlets.user;

import com.mchange.util.AssertException;
import controllers.UserController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.Action;
import servlets.utils.RequestValidation;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class BaseUser implements Action, RequestValidation {

    public static final String ID = "id";
    protected final Logger logger = LoggerFactory.getLogger(BaseUser.class);
    private final EntityManager em = JPAUtil.getEntityManager();
    protected final UserController controller = new UserController(em);

    protected static final String FORWARD_PAGES_USER_FORM_CREATE_USER_JSP = "forward:pages/user/formCreateUser.jsp";
    protected static final String FORWARD_PAGES_FORM_LOGIN_JSP = "forward:pages/formLogin.jsp";
    protected static final String FORWARD_PAGES_USER_FORM_LIST_USER_JSP = "forward:pages/user/formListUser.jsp";
    protected static final String FORWARD_PAGES_USER_FORM_UPDATE_USER_JSP = "forward:pages/user/formUpdateUser.jsp";
    protected static final String FORWARD_PAGES_NOT_FOUND_JSP = "forward:pages/not-found.jsp";
    protected static final String REDIRECT_PRODUCT_ACTION_CREATE_USER = "redirect:product?action=CreateUser";
    protected static final String REDIRECT_USER_ACTION_LIST_USER_BY_ID = "redirect:user?action=ListUser&id=";
    protected static final String USER = "user";
    protected static final String PASSWORD = "password";
    protected static final String CONFIRM_PASSWORD = "confirmPassword";
    protected static final String EMAIL = "email";
    protected static final String ERROR = "error";
    protected static final String SUCCESS = "success";

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.error("Error: BaseUser.execute() is not implemented");
        throw new AssertException("This method should be overridden");
    }

    @Override
    public boolean validate(HttpServletRequest req, HttpServletResponse resp) {
        if (Objects.isNull(req.getParameter(ID))) {
            logger.warn("Error: Id can't be null");
            req.setAttribute(ERROR, "User not found");
            return false;
        }
        return true;
    }
}
