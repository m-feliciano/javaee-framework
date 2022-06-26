package servlets.product;

import domain.Product;
import dto.ProductDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditProduct extends BaseProduct {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET editing product");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        Product product = controller.findById(Long.parseLong(req.getParameter("id")));
        req.setAttribute("product", new ProductDTO(product));
        return "forward:pages/product/formUpdateProduct.jsp";
    }

}
