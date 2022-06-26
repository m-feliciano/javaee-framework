package servlets;

import com.mchange.util.AssertException;
import controllers.UserController;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class BaseLogin implements Action {
    private final EntityManager em = JPAUtil.getEntityManager();
    protected final UserController controller = new UserController(em);
    protected Logger logger = Logger.getLogger(BaseLogin.class.getName());

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.warning("Error: BaseLogin.execute() is not implemented");
        throw new AssertException("Not implemented");
    }
}
