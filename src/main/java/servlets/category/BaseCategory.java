package servlets.category;

import com.mchange.util.AssertException;
import controllers.CategoryController;
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
public class BaseCategory implements Action, RequestValidation {

    protected final Logger logger = LoggerFactory.getLogger(BaseCategory.class);
    private final EntityManager em = JPAUtil.getEntityManager();
    protected final CategoryController controller = new CategoryController(em);

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.error("Error: BaseCategory.execute() is not implemented");
        throw new AssertException("This method should be overridden");
    }

    @Override
    public boolean validate(HttpServletRequest req, HttpServletResponse resp) {
        if (Objects.isNull(req.getParameter("id"))) {
            logger.warn("Error: Id can't be null");
            req.setAttribute("error", "Category not found");
            return false;
        }
        return true;
    }
}
