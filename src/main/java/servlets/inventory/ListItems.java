package servlets.inventory;

import domain.Inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

public class ListItems extends BaseInventory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing items by filter");

        String id = req.getParameter("id");
        if (this.validate(req, resp)) {
            Inventory inventory = controller.findById(Long.parseLong(id));
            if (Objects.isNull(inventory)) {
                return "forward:pages/not-found.jsp";
            }

            req.setAttribute("item", inventory);
            return "forward:pages/inventory/formListItem.jsp";
        }

        String param = req.getParameter("param");
        String value = req.getParameter("value");
        if (!Objects.isNull(param) && !Objects.isNull(value)) {
            List<Inventory> inventories;
            if (param.equals("name")) {
                inventories = controller.findAllByProductName(value);
            } else {
                inventories = controller.findAllByDescription(value);
            }

            req.setAttribute("items", inventories);
        } else {
            req.setAttribute("items", controller.findAll());
        }

        return "forward:pages/inventory/listItems.jsp";
    }

}
