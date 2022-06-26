package servlets.product;

import controllers.ProductController;
import domain.Product;
import dto.ProductDTO;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class EditProduct implements Action {

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
        System.out.println("doGET editing single product");

        if (Objects.isNull(req.getParameter("id"))) {
            req.setAttribute("error", "Product not found");
            return "forward:pages/not-found.jsp";
        }
        Long id = Long.parseLong(req.getParameter("id"));
        Product product = productController.findById(id);
        ProductDTO dto = new ProductDTO(product);
        req.setAttribute("product", dto);
        return "forward:pages/product/formUpdateProduct.jsp";
    }

}
