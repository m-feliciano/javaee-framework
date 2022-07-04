package servlets.product;

import domain.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
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
        if (!Objects.isNull(id)) {
            Product product = controller.findById(Long.parseLong(id));
            if (Objects.isNull(product)) {
                return "forward:pages/not-found.jsp";
            }

            req.setAttribute("product", product);
            return "forward:pages/product/formListProduct.jsp";
        }

        String param = req.getParameter("param");
        String value = req.getParameter("value");
        List<Product> products;
        if (!Objects.isNull(param) && !Objects.isNull(value)) {
            if (param.equals("name")) {
                products = controller.findAllByName(value);
            } else {
                products = controller.findAllByDescription(value);
            }

            req.setAttribute("products", products);
        } else {
            products = controller.findAll();
            req.setAttribute("products", products);
        }
        return "forward:pages/product/listProducts.jsp";
    }

}
