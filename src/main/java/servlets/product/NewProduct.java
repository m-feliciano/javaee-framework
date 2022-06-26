package servlets.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewProduct extends BaseProduct {
    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET redirecting to form createProduct");
        return "forward:pages/product/formCreateProduct.jsp";
    }
}
