package servlets.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListProducts extends BaseProduct {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing products");
        req.setAttribute("products", controller.findAll());
        return "forward:pages/product/listProducts.jsp";
    }

}
