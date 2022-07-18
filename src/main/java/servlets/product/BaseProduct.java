package servlets.product;

import com.mchange.util.AssertException;
import controllers.ProductController;
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
public class BaseProduct implements Action, RequestValidation {

    private final EntityManager em = JPAUtil.getEntityManager();
    protected final ProductController controller = new ProductController(em);
    protected Logger logger = LoggerFactory.getLogger(BaseProduct.class);

    protected static final String FORWARD_PAGES_NOT_FOUND_JSP = "forward:pages/not-found.jsp";
    protected static final String FORWARD_PAGES_PRODUCT_FORM_LIST_PRODUCT_JSP = "forward:pages/product/formListProduct.jsp";
    protected static final String FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP = "forward:pages/product/listProducts.jsp";
    protected static final String FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP = "forward:pages/product/formUpdateProduct.jsp";
    protected static final String FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP = "forward:pages/product/formCreateProduct.jsp";
    protected static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS = "redirect:product?action=ListProducts";
    protected static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID = "redirect:product?action=ListProducts&id=";
    protected static final String ID = "id";
    protected static final String PRODUCT = "product";
    protected static final String PRODUCTS = "products";
    protected static final String ERROR = "error";
    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String URL = "url";
    protected static final String PRICE = "price";

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.error("Error: BaseProduct.execute() is not implemented");
        throw new AssertException("This method should be overridden");
    }

    @Override
    public boolean validate(HttpServletRequest req, HttpServletResponse resp) {
        if (Objects.isNull(req.getParameter(ID))) {
            logger.warn("Error: Id can't be null");
            req.setAttribute(ERROR, "Product not found");
            return false;
        }
        return true;
    }
}
