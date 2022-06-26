package servlets.inventory;

import controllers.ProductController;
import domain.Inventory;
import domain.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public class UpdateItem extends BaseInventory {
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
        logger.info("doGET updating inventory item");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        Product product = productController.findById(Long.parseLong(req.getParameter("productId")));
        Inventory item = controller.findById(Long.parseLong(req.getParameter("id")));
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setDescription(req.getParameter("description"));
        item.setPrice(product.getPrice().multiply(new BigDecimal(quantity)));
        controller.update(item);
        req.setAttribute("item", item);
        return "redirect:inventory?action=ListItem&id=" + item.getId();
    }

}
