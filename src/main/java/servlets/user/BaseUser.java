package servlets.user;

import com.mchange.util.AssertException;
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

import static servlets.base.Base.ERROR;

@Getter
@RequiredArgsConstructor
public class BaseUser implements Action, RequestValidation {

    public static final String ID = "id";
    public static final String ERROR_ID_CAN_T_BE_NULL = "Error: Id can't be null";
    public static final String USER_NOT_FOUND = "User not found";
    protected final Logger logger = LoggerFactory.getLogger(BaseUser.class);
    private final EntityManager em = JPAUtil.getEntityManager();
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
            logger.warn(ERROR_ID_CAN_T_BE_NULL);
            req.setAttribute(ERROR, USER_NOT_FOUND);
            return false;
        }
        return true;
    }

    public EntityManager getEm() {
        if (em.isOpen()) {
            return em;
        } else {
            return JPAUtil.getEntityManager();
        }
    }
}
