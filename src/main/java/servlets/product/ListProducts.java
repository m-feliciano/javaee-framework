package servlets.product;

import domain.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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
        logger.info("doGET listing products by filter");

        String id = req.getParameter("id");
        if (this.validate(req, resp)) {
            Product product = controller.findById(Long.parseLong(id));
            if (Objects.isNull(product)) {
                return "forward:pages/not-found.jsp";
            }

            req.setAttribute("product", product);
            return "forward:pages/product/formListProduct.jsp";
        }

        String param = req.getParameter("param");
        String value = req.getParameter("value");
        if (!Objects.isNull(param) && !Objects.isNull(value)) {
            if (param.equals("name")) {
                req.setAttribute("products", controller.findAllByName(value));
            } else {
                req.setAttribute("products", controller.findAllByDescription(value));
            }
        } else {
            req.setAttribute("products", controller.findAll());
        }
        return "forward:pages/product/listProducts.jsp";
    }

}
