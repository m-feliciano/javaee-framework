package servlets.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class ListProductsByName extends BaseProduct {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing products by name");
        String name = req.getParameter("name");
        if (Objects.isNull(name)) {
            req.setAttribute("error", "Product not found");
            return "forward:pages/not-found.jsp";
        }

        req.setAttribute("products", controller.findAllByName(name));
        return "forward:pages/product/listProducts.jsp";
    }

}
