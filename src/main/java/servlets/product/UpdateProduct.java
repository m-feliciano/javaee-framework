package servlets.product;

import domain.Product;
import utils.CurrencyFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateProduct extends BaseProduct {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating product");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        Product product = controller.findById(Long.parseLong(req.getParameter("id")));
        product.setName(req.getParameter("name"));
        product.setDescription(req.getParameter("description"));
        product.setPrice(CurrencyFormatter.stringToBigDecimal(req.getParameter("price")));
        product.setUrl(req.getParameter("url"));
        controller.update(product);
        req.setAttribute("product", product);
        return "redirect:product?action=ListProduct&id=" + product.getId();
    }

}
