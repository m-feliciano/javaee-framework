package servlets.product;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.CurrencyFormatter;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class UpdateProduct implements Action {

    private final EntityManager em = JPAUtil.getEntityManager();
    private final ProductController productController = new ProductController(em);

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("doPOST update product");

        if (Objects.isNull(req.getParameter("id"))) {
            req.setAttribute("error", "Product not found");
            return "forward:pages/not-found.jsp";
        }

        Long id = Long.parseLong(req.getParameter("id"));
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceString = req.getParameter("price");
        String url = req.getParameter("url");
        Product product = productController.findById(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(CurrencyFormatter.stringToBigDecimal(priceString));
        product.setUrl(url);
        productController.update(product);
        req.setAttribute("product", product);
        return "redirect:product?action=ListProducts";
    }

}
