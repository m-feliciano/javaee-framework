package servlets.inventory;

import controllers.InventoryController;
import domain.Inventory;
import servlets.Action;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ListItems implements Action {

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
        System.out.println("doGET listing inventory items");
        List<Inventory> items = inventoryController.findAll();
        req.setAttribute("items", items);
        System.out.println(items);
        return "forward:pages/inventory/listItems.jsp";
    }

}
