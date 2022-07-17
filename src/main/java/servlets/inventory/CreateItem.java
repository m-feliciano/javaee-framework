package servlets.inventory;

import controllers.ProductController;
import domain.Inventory;
import domain.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public class CreateItem extends BaseInventory {
    private final ProductController productController = new ProductController(this.getEm());

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating a inventory item");

        Product product = productController.findById(Long.parseLong(req.getParameter("productId")));
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        Inventory item = new Inventory(
                product,
                quantity,
                req.getParameter("description"));
        controller.save(item);
        req.setAttribute("item", item);
        return "redirect:inventory?action=ListItems&id=" + item.getId();
    }

}
