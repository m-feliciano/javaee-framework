package servlets.product;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

public class ListProductsByName implements Action {

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
        System.out.println("doGET listing products by name");
        if (!Objects.isNull(req.getParameter("id"))) {
            String name = req.getParameter("name");
            List<Product> list = productController.findAllByName(name);
            req.setAttribute("products", list);
        }

        return "forward:pages/product/listProducts.jsp";
    }

}
