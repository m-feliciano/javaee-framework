package servlets;

import com.mchange.util.AssertException;
import controllers.UserController;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseLogin implements Action {
    private final EntityManager em = JPAUtil.getEntityManager();
    protected final UserController controller = new UserController(em);
    protected Logger logger = LoggerFactory.getLogger(BaseLogin.class.getName());

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.warn("Error: BaseLogin.execute() is not implemented");
        throw new AssertException("Not implemented");
    }
}
