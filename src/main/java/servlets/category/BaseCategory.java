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

    protected static final String FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP = "forward:pages/category/formCreateCategory.jsp";
    protected static final String FORWARD_PAGES_CATEGORY_LIST_CATEGORIES_JSP = "forward:pages/category/listCategories.jsp";
    protected static final String FORWARD_PAGES_CATEGORY_FORM_LIST_CATEGORY_JSP = "forward:pages/category/formListCategory.jsp";
    protected static final String FORWARD_PAGES_CATEGORY_FORM_UPDATE_CATEGORY_JSP = "forward:pages/category/formUpdateCategory.jsp";
    protected static final String FORWARD_PAGES_NOT_FOUND_JSP = "forward:pages/not-found.jsp";
    protected static final String REDIRECT_CATEGORY_ACTION_LIST_CATEGORIES = "redirect:category?action=ListCategories";
    protected static final String REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID = "redirect:category?action=ListCategory&id=";
    protected static final String ERROR = "error";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String CATEGORY = "category";
    protected static final String CATEGORIES = "categories";


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
            req.setAttribute(ERROR, "Category not found");
            return false;
        }
        return true;
    }
}
