package servlets.product;

import controllers.ProductController;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class DeleteProduct implements Action {

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
        System.out.println("doPOST deleting product");

        if (!Objects.isNull(req.getParameter("id"))) {
            Long id = Long.parseLong(req.getParameter("id"));
            productController.delete(id);
        }
        return "redirect:product?action=ListProducts";
    }

}
