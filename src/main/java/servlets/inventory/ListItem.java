package servlets.inventory;

import controllers.InventoryController;
import domain.Inventory;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class ListItem implements Action {

    private final EntityManager em = JPAUtil.getEntityManager();
    private final InventoryController inventoryController = new InventoryController(em);

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("doGET listing single inventory");

        if (!Objects.isNull(req.getParameter("id"))) {
            Long id = Long.parseLong(req.getParameter("id"));
            Inventory item = inventoryController.findById(id);

            if (item != null) {
                req.setAttribute("item", item);
                return "forward:pages/inventory/formListItem.jsp";
            }
        }

        return "forward:pages/not-found.jsp";
    }

}
